package routes

import (
	"coffee_shop_backend/services"

	"github.com/gin-gonic/gin"
)

func Routes(router *gin.Engine) *gin.Engine {
	router.POST("/user", services.UsersGroup)
	router.GET("/user", services.UsersGroup)
	// router.GET("/location", location.GetLocation)
	// router.GET("/propertytype", propertytype.GetPropertyType)
	return router
}
