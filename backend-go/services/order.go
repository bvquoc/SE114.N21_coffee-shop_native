package services

import (
	app_context "coffee_shop_backend/context"
	"coffee_shop_backend/models"
	"encoding/json"
	"fmt"
	"net/http"

	"github.com/gin-gonic/gin"
)

func OrderCreate(c *gin.Context) {
	var newOrder models.Order
	if err := c.ShouldBindJSON(&newOrder); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"message": "Invalid request body"})
		return
	}

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

	c.JSON(http.StatusCreated, map[string]interface{}{"orderID": ordersId})
}
