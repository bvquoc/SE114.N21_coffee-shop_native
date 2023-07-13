package services

import (
	"coffee_shop_backend/models"

	"github.com/gin-gonic/gin"
)

func UserCreateUser(c *gin.Context) {
	var newUser models.User
	if err := c.ShouldBindJSON(&newUser); err != nil {
		c.JSON(400, gin.H{"message": "Invalid request body"})
		return
	}

	// users = append(users, newUser)
	// c.JSON(201)
}
