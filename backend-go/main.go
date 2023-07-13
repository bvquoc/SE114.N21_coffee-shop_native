package main

import (
	app_context "coffee_shop_backend/context"
	"coffee_shop_backend/db"
	"coffee_shop_backend/routes"
	"log"
	"os"

	"github.com/gin-gonic/gin"
	"github.com/joho/godotenv"
)

func main() {
	godotenv.Load(".env")

	firestoreClient, err := db.NewFirestoreClient(os.Getenv("PROJECT_ID"))
	if err != nil {
		log.Fatalln(err)
	}
	app_context.AppFirestoreClient = firestoreClient

	g := gin.Default()
	g = routes.Routes(g)
	g.Run()
}
