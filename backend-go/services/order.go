package services

import (
	"net/http"

	"github.com/gin-gonic/gin"
)

func OrderGetAll(c *gin.Context) {
	c.JSON(http.StatusOK, gin.H{"orders": make([]int, 0)})
}
