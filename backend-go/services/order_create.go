package services

import (
	"coffee_shop_backend/constants"
	app_context "coffee_shop_backend/context"
	ds "coffee_shop_backend/datastruct"
	"coffee_shop_backend/helpers"
	"coffee_shop_backend/models"
	"encoding/json"
	"fmt"
	"math"
	"net/http"
	"sync"
	"time"

	"github.com/gin-gonic/gin"
)

func OrderCreate(c *gin.Context) {
	var newOrder models.Order
	if err := c.ShouldBindJSON(&newOrder); err != nil || newOrder.IDUser == "" {
		c.JSON(http.StatusBadRequest, gin.H{"message": "Invalid request body"})
		return
	}

	// user, err := app_context.App.GetDocumentMap("users", newOrder.IDUser)
	// if err != nil {
	// 	c.JSON(http.StatusBadRequest, gin.H{"message": "Permission denied! User is not created."})
	// 	return
	// }
	// fmt.Println(user)

	var orderId string
	newOrder.Status = constants.ORDER_CREATED
	dataBytes, err := json.Marshal(newOrder)
	if err != nil {
		fmt.Println("Error marshaling struct:", err)
		c.JSON(http.StatusBadRequest, gin.H{"message": "Invalid request body"})
		return
	}
	var data map[string]interface{}
	json.Unmarshal(dataBytes, &data)
	delete(data, "orderedFoods")
	delete(data, "discountPrice")
	delete(data, "totalPrice")
	delete(data, "totalProduct")
	delete(data, "pickupTime")
	delete(data, "address")
	delete(data, "deliveryCost")
	if data["promo"] == "" {
		delete(data, "promo")
	}
	ordersId, err := app_context.App.CreateDocument(constants.CLT_ORDER, data)
	if err != nil {
		fmt.Println("Error create firestore document:", err)
		c.JSON(http.StatusBadRequest, gin.H{"message": "Invalid request body"})
		return
	}
	orderId = ordersId
	c.JSON(http.StatusCreated, gin.H{"orderID": ordersId})

	go func() {
		newOrder.Status = constants.ORDER_PROCESSING
		calcPrice(&newOrder)

		dataBytes, _ := json.Marshal(newOrder)
		var data map[string]interface{}
		json.Unmarshal(dataBytes, &data)
		delete(data, "dateOrder")
		delete(data, "promo")
		delete(data, "store")
		delete(data, "user")
		delete(data, "toppingIdList")
		if (newOrder.PickupTime == time.Time{}) {
			delete(data, "pickupTime")
		} else {
			delete(data, "address")
		}

		app_context.App.UpdateDocument(constants.CLT_ORDER, orderId, data)
	}()
}

func calcPrice(ord *models.Order) {
	ord.PriceProducts = 0

	if (ord.PickupTime == time.Time{}) {
		fmt.Println("[ORDER_CREATE] Delivery")
		respMpStore, _ := app_context.App.GetDocumentMap(constants.CLT_STORE, ord.IDStore)
		store := helpers.ToStore(respMpStore)
		distanceInKm := int(math.Floor(helpers.CalculateDistance(store.Address.Lat, store.Address.Lng, ord.Address.Lat, ord.Address.Lng)))
		ord.DeliveryCost = helpers.CalcShippingFee(distanceInKm)
	} else {
		fmt.Println("[ORDER_CREATE] Pickup")
	}

	setFoodId := make(ds.SetOfString)
	setToppingId := make(ds.SetOfString)
	setSizeId := make(ds.SetOfString)

	for _, v := range ord.OrderedFoods {
		setSizeId.Add(v.Size)
		setFoodId.Add(v.ID)

		for _, t := range v.ToppingIds {
			setToppingId.Add(t)
		}
	}

	mapSize := make(map[string]models.Size)
	mapTopping := make(map[string]models.Topping)
	mapFood := make(map[string]models.Food)

	var wg sync.WaitGroup
	wg.Add(3)
	go func() { // fetchSizes
		defer wg.Done()
		for sizeId := range setSizeId {
			respMpSize, _ := app_context.App.GetDocumentMap(constants.CLT_SIZE, sizeId)
			mapSize[sizeId] = helpers.ToSize(respMpSize)
		}
	}()
	go func() { // fetchToppings
		defer wg.Done()
		for toppingId := range setToppingId {
			respMpTopping, _ := app_context.App.GetDocumentMap(constants.CLT_TOPPING, toppingId)
			mapTopping[toppingId] = helpers.ToTopping(respMpTopping)
		}
	}()
	go func() { // fetchFoods
		defer wg.Done()
		for foodId := range setFoodId {
			respMpFood, _ := app_context.App.GetDocumentMap(constants.CLT_FOOD, foodId)
			mapFood[foodId] = helpers.ToFood(respMpFood)
		}
	}()
	wg.Wait()

	for i, v := range ord.OrderedFoods {
		unitPrice := 0
		ord.OrderedFoods[i].Image = mapFood[v.ID].Images[0]
		// size
		sizePrice := mapSize[v.Size].Price
		unitPrice += sizePrice
		ord.OrderedFoods[i].Size = mapSize[v.Size].Name
		// food
		foodPrice := mapFood[v.ID].Price
		unitPrice += foodPrice
		ord.OrderedFoods[i].Name = mapFood[v.ID].Name
		// topping
		for _, t := range v.ToppingIds {
			unitPrice += mapTopping[t].Price
			str := ord.OrderedFoods[i].ToppingsStr
			if str != "" {
				str = str + ", "
			}
			str = str + mapTopping[t].Name
			ord.OrderedFoods[i].ToppingsStr = str
		}

		totalPrice := unitPrice * v.Quantity
		ord.OrderedFoods[i].UnitPrice = unitPrice
		ord.OrderedFoods[i].TotalPrice = totalPrice
		ord.PriceProducts += totalPrice
	}

	if len(ord.IDPromo) > 0 {
		respPromo, err := app_context.App.GetDocumentMap("Promo", ord.IDPromo)
		if err != nil {
			fmt.Println("Invalid promo code")
		} else {
			promo := helpers.ToPromo(respPromo)
			// fmt.Print(promo)
			canUse := false
			for _, v := range promo.StoreIdList {
				if v == ord.IDStore {
					canUse = true
					break
				}
			}

			if canUse {
				canUse = false

				if ord.PriceProducts >= promo.AtLeastPrice {
					ord.PriceDiscount = int(math.Min(float64(ord.PriceProducts)*respPromo["percent"].(float64), float64(promo.MaxDiscount)))
				}
			}
		}
	}

	ord.PriceTotal = int(math.Max(0.0, float64(ord.PriceProducts-ord.PriceDiscount+ord.DeliveryCost)))
}
