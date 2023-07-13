package routes

import (
	"coffee_shop_backend/services"

	"github.com/gin-gonic/gin"
)

func Routes(router *gin.Engine) *gin.Engine {
	router.GET("/", func(c *gin.Context) {
		c.JSON(200, gin.H{
			"message": "Coffee Shop Apis!",
		})
	})

	// Create a new user
	router.POST("/users", services.UserCreateUser)

	// Create an order
	router.POST("/orders", services.OrderCreate)

	return router
}
