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

	// USER GROUP
	router.GET("/users", services.UserGetAll)
	router.POST("/users", services.UserCreateUser)

	// ORDER GROUP
	router.GET("/orders", services.OrderGetAll)
	router.POST("/orders", services.OrderCreate)
	router.PUT("/orders/:orderId", services.OrderChangeStatus)

	return router
}
