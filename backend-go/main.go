package main

import (
	app_context "coffee_shop_backend/context"
	fireclient "coffee_shop_backend/firebase"
	"coffee_shop_backend/routes"
	"log"
	"os"

	"github.com/gin-gonic/gin"
	"github.com/joho/godotenv"
)

func main() {
	godotenv.Load(".env")

	firestoreClient, err := fireclient.NewFirestoreClient(os.Getenv("PROJECT_ID"))
	if err != nil {
		log.Fatalln(err)
	}
	app_context.App = firestoreClient

	// ctx := context.Background()
	// app, err := firebase.NewApp(ctx, nil)
	// if err != nil {
	// 	log.Fatalf("Failed to create Firebase app: %v", err)
	// }

	// auth, err := app.Auth(ctx)
	// if err != nil {
	// 	log.Fatalf("Failed to get Auth client: %v", err)
	// }

	// Create a new user account
	// params := (&firebase.UserToCreate{}).
	// 	Email("user@example.com").
	// 	EmailVerified(false).
	// 	Password("password123").
	// 	DisplayName("John Doe").
	// 	Disabled(false)

	// user, err := auth.CreateUser(ctx, params)
	// if err != nil {
	// 	log.Fatalf("Failed to create user: %v", err)
	// }

	// log.Printf("User created: %v", user)

	g := gin.Default()
	g = routes.Routes(g)
	g.Run()
}
