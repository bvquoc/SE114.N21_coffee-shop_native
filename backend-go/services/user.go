package services

import (
	"net/http"

	"github.com/gin-gonic/gin"
)

func UsersGroup(c *gin.Context) {

	// var searchParams models.SearchParams
	// if err := c.BindJSON(&searchParams); err != nil {
	// 	log.Fatalf("Can not parse search params: %v", err)
	// }

	// var searchResult = models.SearchResult{}
	// searchResult.Hits = make([]interface{}, 0)

	// if len(searchParams.Status) == 0 {
	// 	searchResult.Total = 0
	// 	c.JSON(http.StatusOK, gin.H{
	// 		"status": "OK",
	// 		"result": searchResult,
	// 	})
	// 	return
	// }

	// searchResult = searchlogic.BasicSearch(searchParams, utils.EsClient)

	c.JSON(http.StatusOK, gin.H{
		"status": "OK",
		"result": "user",
	})
}
