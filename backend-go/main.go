package main

import (
	"coffee_shop_backend/routes"
	"net/http"

	"github.com/gin-gonic/gin"
	"github.com/joho/godotenv"
)

func main() {
	godotenv.Load(".env")
	// run the server mode
	// utils.InitParamData()
	runHTTPServer()
}

func runHTTPServer() {
	// wg := &sync.WaitGroup{}
	// wg.Add(2)

	// go func() {
	// 	defer wg.Done()
	// 	// go firestore.initialapp
	// }()
	// wg.Wait()

	g := gin.Default()
	g = routes.Routes(g)
	g.GET("/ping", func(c *gin.Context) {
		c.JSON(http.StatusOK, gin.H{
			"message": "pong",
		})
	})
	g.Run()
}
