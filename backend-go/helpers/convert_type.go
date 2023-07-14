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

	if imgs, ok := data["images"].([]interface{}); ok {
		food.Images = make([]string, len(imgs))
		for i, img := range imgs {
			food.Images[i] = fmt.Sprint(img)
		}
	}

	return food
}

/* For model Address */

func ToAddress(data map[string]interface{}) models.Address {
	var address models.Address

	if addressNote, ok := data["addressNote"].(string); ok {
		address.AddressNote = addressNote
	}

	if formattedAddress, ok := data["formattedAddress"].(string); ok {
		address.FormattedAddress = formattedAddress
	}

	if lat, ok := data["lat"].(float64); ok {
		address.Lat = lat
	}

	if lng, ok := data["lng"].(float64); ok {
		address.Lng = lng
	}

	if nameReceiver, ok := data["nameReceiver"].(string); ok {
		address.NameReceiver = nameReceiver
	}

	if phone, ok := data["phone"].(string); ok {
		address.Phone = phone
	}

	return address
}

/* For model Store */

func ToStore(data map[string]interface{}) models.Store {
	var store models.Store

	if name, ok := data["shortName"].(string); ok {
		store.Name = name
	}

	if addressData, ok := data["address"].(map[string]interface{}); ok {
		store.Address = ToAddress(addressData)
	}

	return store
}
