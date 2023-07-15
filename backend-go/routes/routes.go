package routes

import (
	"coffee_shop_backend/services"

	_ "coffee_shop_backend/docs"

	"github.com/gin-gonic/gin"
	swaggerFiles "github.com/swaggo/files"
	ginSwagger "github.com/swaggo/gin-swagger"
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
	// docs route
	router.GET("/orders", services.OrderGetAll)
	router.POST("/orders", services.OrderCreate)
	router.PUT("/orders/:orderId", services.OrderChangeStatus)

	router.GET("/docs/*any", ginSwagger.WrapHandler(swaggerFiles.Handler))

	return router
}
