package services

import (
	"coffee_shop_backend/constants"
	app_context "coffee_shop_backend/context"
	"net/http"

	"github.com/gin-gonic/gin"
)

func OrderChangeStatus(c *gin.Context) {
	orderID := c.Param("orderId")

	var requestBody struct {
		Status string `json:"status"`
	}
	if err := c.ShouldBindJSON(&requestBody); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"orderId": orderID, "error": err.Error()})
		return
	}
	if !constants.IsOrderStatus(requestBody.Status) {
		if err := c.ShouldBindJSON(&requestBody); err != nil {
			c.JSON(http.StatusBadRequest, gin.H{"orderId": orderID, "error": "Invalid order status"})
			return
		}
	}

	data := make(map[string]interface{})
	data["status"] = requestBody.Status
	err := app_context.App.UpdateDocument(constants.CLT_ORDER, orderID, data)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"orderId": orderID, "error": "Invalid order ID"})
		return
	}

	c.JSON(http.StatusOK, gin.H{"orderId": orderID, "status": requestBody.Status})
}
