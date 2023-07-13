package helpers

import (
	"coffee_shop_backend/models"
	"fmt"
	"strconv"
)

/* For model Size */

func ToSize(data map[string]interface{}) models.Size {
	var size models.Size

	if imageUrl, ok := data["image"].(string); ok {
		size.ImageUrl = imageUrl
	}

	if name, ok := data["name"].(string); ok {
		size.Name = name
	}

	price, err := strconv.Atoi(fmt.Sprint(data["price"]))
	if err == nil {
		size.Price = price
	}

	return size
}

/* For model Topping */

func ToTopping(data map[string]interface{}) models.Topping {
	var topping models.Topping

	if imageUrl, ok := data["image"].(string); ok {
		topping.ImageUrl = imageUrl
	}

	if name, ok := data["name"].(string); ok {
		topping.Name = name
	}

	price, err := strconv.Atoi(fmt.Sprint(data["price"]))
	if err == nil {
		topping.Price = price
	}

	return topping
}

/* For model Food */

func ToFood(data map[string]interface{}) models.Food {
	var food models.Food

	if name, ok := data["name"].(string); ok {
		food.Name = name
	}

	price, err := strconv.Atoi(fmt.Sprint(data["price"]))
	if err == nil {
		food.Price = price
	}

	return food
}
