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

	router.POST("/orders", services.OrderCreate)
	// router.GET("/location", location.GetLocation)
	// router.GET("/propertytype", propertytype.GetPropertyType)
	return router
}
