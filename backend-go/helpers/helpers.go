package helpers

import (
	"coffee_shop_backend/models"
	"fmt"
	"math/rand"
	"strconv"
	"strings"
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

/* Functions */

var (
	lowerCharSet   = "abcdedfghijklmnopqrst"
	upperCharSet   = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
	specialCharSet = "!@#$%&*"
	numberSet      = "0123456789"
	allCharSet     = lowerCharSet + upperCharSet + specialCharSet + numberSet
)

func GeneratePassword(passwordLength, minSpecialChar, minNum, minUpperCase int) string {
	var password strings.Builder

	//Set special character
	for i := 0; i < minSpecialChar; i++ {
		random := rand.Intn(len(specialCharSet))
		password.WriteString(string(specialCharSet[random]))
	}

	//Set numeric
	for i := 0; i < minNum; i++ {
		random := rand.Intn(len(numberSet))
		password.WriteString(string(numberSet[random]))
	}

	//Set uppercase
	for i := 0; i < minUpperCase; i++ {
		random := rand.Intn(len(upperCharSet))
		password.WriteString(string(upperCharSet[random]))
	}

	remainingLength := passwordLength - minSpecialChar - minNum - minUpperCase
	for i := 0; i < remainingLength; i++ {
		random := rand.Intn(len(allCharSet))
		password.WriteString(string(allCharSet[random]))
	}
	inRune := []rune(password.String())
	rand.Shuffle(len(inRune), func(i, j int) {
		inRune[i], inRune[j] = inRune[j], inRune[i]
	})
	return string(inRune)
}
