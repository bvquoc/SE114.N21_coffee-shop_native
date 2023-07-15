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
	Address       Address     `json:"address"`
	PriceDiscount int         `json:"discountPrice"`
	PriceTotal    int         `json:"totalPrice"`
	PriceProducts int         `json:"totalProduct"`
	DeliveryCost  int         `json:"deliveryCost"`
}
