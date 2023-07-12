package main

import (
	"coffee_shop_backend/firestore"
	"coffee_shop_backend/models"
	"fmt"
	"log"
	"os"

	"github.com/joho/godotenv"
)

func main() {
	godotenv.Load(".env")

	firestoreClient, err := firestore.NewFirestoreClient(os.Getenv("PROJECT_ID"))
	if err != nil {
		log.Fatalln(err)
	}

	fmt.Println(firestoreClient.GetDocumentMap("Food", "AmericanoDa"))

	user := models.NewUser(map[string]interface{}{
		"name":    "Bui Vi Quoc",
		"isAdmin": true,
	})
	fmt.Println()
	fmt.Println(user)
	// g := gin.Default()
	// g = routes.Routes(g)
	// g.GET("/ping", func(c *gin.Context) {
	// 	c.JSON(http.StatusOK, gin.H{
	// 		"message": "pong",
	// 	})
	// })
	// g.Run()

}
