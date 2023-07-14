package services

import (
	"coffee_shop_backend/constants"
	app_context "coffee_shop_backend/context"
	"coffee_shop_backend/datastruct"
	"coffee_shop_backend/helpers"
	"coffee_shop_backend/models"
	"encoding/json"
	"fmt"
	"math"
	"net/http"
	"time"

	"github.com/gin-gonic/gin"
)

func OrderCreate(c *gin.Context) {
	var newOrder models.Order
	if err := c.ShouldBindJSON(&newOrder); err != nil || newOrder.IDUser == "" {
		c.JSON(http.StatusBadRequest, gin.H{"message": "Invalid request body"})
		return
	}

	// user, err := app_context.AppFirestoreClient.GetDocumentMap("users", newOrder.IDUser)
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
	ordersId, err := app_context.AppFirestoreClient.CreateDocument("beorders", data)
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
		delete(data, "orderedFoods")
		delete(data, "dateOrder")
		delete(data, "pickupTime")
		delete(data, "address")
		delete(data, "promo")
		delete(data, "store")
		delete(data, "user")
		json.Unmarshal(dataBytes, &data)

		app_context.AppFirestoreClient.UpdateDocument("beorders", orderId, data)
	}()
}

func calcPrice(ord *models.Order) {
	ord.PriceProducts = 0

	if (ord.PickupTime == time.Time{}) {
		fmt.Println("[ORDER_CREATE] Delivery")
	} else {
		fmt.Println("[ORDER_CREATE] Pickup")
	}

	setFoodId := make(datastruct.SetOfString)
	setToppingId := make(datastruct.SetOfString)
	setSizeId := make(datastruct.SetOfString)

	for _, v := range ord.OrderedFoods {
		setSizeId.Add(v.Size)
		setFoodId.Add(v.ID)
		for _, t := range v.Toppings {
			setToppingId.Add(t)
		}
	}

	mapSize := make(map[string]models.Size)
	for sizeId := range setSizeId {
		respMpSize, _ := app_context.AppFirestoreClient.GetDocumentMap(constants.CLT_SIZE, sizeId)
		mapSize[sizeId] = helpers.ToSize(respMpSize)
	}
	mapTopping := make(map[string]models.Topping)
	for toppingId := range setToppingId {
		respMpTopping, _ := app_context.AppFirestoreClient.GetDocumentMap(constants.CLT_TOPPING, toppingId)
		mapTopping[toppingId] = helpers.ToTopping(respMpTopping)
	}
	mapFood := make(map[string]models.Food)
	for foodId := range setFoodId {
		respMpFood, _ := app_context.AppFirestoreClient.GetDocumentMap(constants.CLT_FOOD, foodId)
		mapFood[foodId] = helpers.ToFood(respMpFood)
	}

	for _, v := range ord.OrderedFoods {
		sl := v.Quantity
		// size
		sizePrice := mapSize[v.Size].Price
		ord.PriceProducts += sizePrice * sl
		// food
		foodPrice := mapFood[v.ID].Price
		ord.PriceProducts += foodPrice * sl
		// topping
		for _, t := range v.Toppings {
			ord.PriceProducts += mapTopping[t].Price * sl
		}
	}

	if len(ord.IDPromo) > 0 {
		respPromo, _ := app_context.AppFirestoreClient.GetDocumentMap("Promo", ord.IDPromo)

		canUse := false
		for _, v := range respPromo["stores"].([]interface{}) {
			if s, ok := v.(string); ok {
				if s == ord.IDStore {
					canUse = true
					break
				}
			}
		}

		if canUse {
			canUse = false
			atLeastPrice := int(respPromo["minPrice"].(int64))
			maxDiscount := int(respPromo["maxPrice"].(int64))

			if ord.PriceProducts >= atLeastPrice {
				ord.PriceDiscount = int(math.Min(float64(ord.PriceProducts)*respPromo["percent"].(float64), float64(maxDiscount)))
			}
		}

	}

	ord.PriceTotal = int(math.Max(0.0, float64(ord.PriceProducts-ord.PriceDiscount+ord.DeliveryCost)))
}
