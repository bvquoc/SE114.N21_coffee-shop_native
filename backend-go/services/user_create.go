package services

import (
	"coffee_shop_backend/constants"
	app_context "coffee_shop_backend/context"
	"coffee_shop_backend/helpers"
	"coffee_shop_backend/models"
	"encoding/json"
	"fmt"
	"net/http"

	"cloud.google.com/go/firestore"
	"github.com/gin-gonic/gin"
)

func UserCreateUser(c *gin.Context) {
	var newUser models.User
	if err := c.ShouldBindJSON(&newUser); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"message": "Invalid request body"})
		return
	}

	if newUser.Role != constants.ROLE_ADMIN && newUser.Role != constants.ROLE_STAFF && newUser.Role != constants.ROLE_SUPER_ADMIN {
		c.JSON(http.StatusBadRequest, gin.H{"message": "Invalid request body, wrong role"})
		return
	}

	if newUser.Role == constants.ROLE_STAFF && newUser.StaffOf == "" {
		c.JSON(http.StatusBadRequest, gin.H{"message": "Invalid request body, missing store field"})
		return
	}

	password := helpers.GeneratePassword(8, 1, 1, 1)

	newUser.AvatarURL = constants.AVATAR_URL
	newUser.CoverURL = constants.COVER_URL
	newUser.IsStaff = true
	if newUser.Role == constants.ROLE_ADMIN {
		newUser.IsAdmin = true
	}
	if newUser.Role == constants.ROLE_SUPER_ADMIN {
		newUser.IsAdmin = true
		newUser.IsSuperAdmin = true
	}

	acc, err := app_context.App.CreateUser(newUser.Email, password, newUser.Name)
	if err != nil {
		c.JSON(http.StatusBadRequest, err)
		return
	}

	newUser.ID = acc.UID
	c.JSON(http.StatusCreated, gin.H{
		"message":  fmt.Sprintf("Created %s account", newUser.Role),
		"email":    newUser.Email,
		"password": password,
		"uid":      acc.UID,
	})

	go func() {
		dataBytes, err := json.Marshal(newUser)
		if err != nil {
			fmt.Println("Error marshaling struct:", err)
			c.JSON(http.StatusBadRequest, gin.H{"message": "Invalid request body"})
			return
		}
		var data map[string]interface{}
		json.Unmarshal(dataBytes, &data)
		delete(data, "role")
		delete(data, "id")
		data["createAt"] = firestore.ServerTimestamp
		app_context.App.CreateDocumentWithId(constants.CLT_USER, acc.UID, data)
	}()
}
