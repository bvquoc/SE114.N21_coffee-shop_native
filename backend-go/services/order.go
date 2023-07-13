package services

import (
	app_context "coffee_shop_backend/context"
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

	user, err := app_context.AppFirestoreClient.GetDocumentMap("users", newOrder.IDUser)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"message": "Permission denied! User is not created."})
		return
	}

	fmt.Println(user)

	dataBytes, err := json.Marshal(newOrder)
	if err != nil {
		fmt.Println("Error marshaling struct:", err)
		c.JSON(http.StatusBadRequest, gin.H{"message": "Invalid request body"})
		return
	}

	var data map[string]interface{}
	json.Unmarshal(dataBytes, &data)

	// ordersId, err := app_context.AppFirestoreClient.CreateDocument("beorders", data)
	// if err != nil {
	// 	fmt.Println("Error create firestore document:", err)
	// 	c.JSON(http.StatusBadRequest, gin.H{"message": "Invalid request body"})
	// 	return
	// }

	// newOrder.PriceDiscount = 0
	// newOrder.PriceTotal = 100
	// newOrder.PriceProducts = 100
	calcPrice(&newOrder)

	c.JSON(http.StatusCreated, newOrder)

	// c.JSON(http.StatusCreated, map[string]interface{}{"orderID": ordersId})
}

func calcPrice(ord *models.Order) {
	ord.PriceProducts = 0

	if (ord.PickupTime == time.Time{}) {
		fmt.Println("Deliver")
	} else {
		fmt.Println("pickup")
	}

	for _, v := range ord.OrderedFoods {
		sl := v.Quantity

		// size
		respSize, _ := app_context.AppFirestoreClient.GetDocumentMap("Size", v.Size)
		sizePrice := respSize["price"].(int64)
		ord.PriceProducts += sizePrice * int64(sl)

		// // food
		respFood, _ := app_context.AppFirestoreClient.GetDocumentMap("Food", v.ID)
		foodPrice := respFood["price"].(int64)
		ord.PriceProducts += foodPrice * int64(sl)

		// topping
		// for _, t := range v.Toppings {
		// 	fmt.Println(t)
		// 	respTopping, _ := app_context.AppFirestoreClient.GetDocumentMap("Topping", t)
		// 	fmt.Println(respTopping["price"])
		// 	toppingPrice := respTopping["price"]

		// 	toppingPrice, ok := respTopping["price"].(int64)
		// 	if !ok {
		// 		fmt.Println("Failed to convert the 'price' field to int64")
		// 	} else {
		// 		ord.PriceProducts += toppingPrice.(int64) * int64(sl)
		// 	}

		// }
	}

	if len(ord.IDPromo) > 0 {
		ord.PriceDiscount = 1
	}

	ord.PriceTotal = int64(math.Max(0.0, float64(ord.PriceProducts-ord.PriceDiscount+ord.DeliveryCost)))
}
