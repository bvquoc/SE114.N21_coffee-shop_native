package models

import "time"

type OrderStatus struct {
}

type Order struct {
	IDUser        string      `json:"user"`
	IDStore       string      `json:"store"`
	IDPromo       string      `json:"promo"`
	DateOrder     time.Time   `json:"dateOrder"`
	Status        string      `json:"status"`
	OrderedFoods  []FoodOrder `json:"orderedFoods"`
	PickupTime    time.Time   `json:"pickupTime"`
	PriceDiscount int64       `json:"discountPrice"`
	PriceTotal    int64       `json:"totalPrice"`
	PriceProducts int64       `json:"totalProduct"`
	DeliveryCost  int64       `json:"deliveryCost"`
}
