package main

import (
	app_context "coffee_shop_backend/context"
	fireclient "coffee_shop_backend/firebase"
	"coffee_shop_backend/routes"
	"fmt"
	"log"
	"os"

	_ "coffee_shop_backend/docs"

	"github.com/gin-gonic/gin"
	"github.com/joho/godotenv"
)

// @title Coffee Shop APIs
// @version 1.0
// @description This is a coffee shop server.

// @contact.name Bui Vi Quoc
// @contact.url https://www.facebook.com/bviquoc/
// @contact.email 21520095@gm.uit.edu.vn

// @host localhost:8080
// @BasePath /
// @query.collection.format multi
func main() {
	defer func() {
		if r := recover(); r != nil {
			fmt.Println("Recovered from panic:", r)
		}
	}()

	godotenv.Load(".env")

	firestoreClient, err := fireclient.NewFirestoreClient(os.Getenv("PROJECT_ID"))
	if err != nil {
		log.Fatalln(err)
	}
	app_context.App = firestoreClient

	g := gin.Default()
	g = routes.Routes(g)
	g.Run(":" + os.Getenv("PORT"))
}
