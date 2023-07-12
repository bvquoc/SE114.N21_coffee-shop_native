package models

import "time"

type OrderStatus struct {
}

type Order struct {
	IDUser      string    `json:"idUser"`
	IDStore     string    `json:"idStore"`
	IDPromo     string    `json:"idPromo"`
	DateOrder   time.Time `json:"dateOrder"`
	Status      string    `json:"status"`
	OrderedFood []Food    `json:"orderedFood"`
	PickupTime  time.Time `json:"pickupTime"`
}
