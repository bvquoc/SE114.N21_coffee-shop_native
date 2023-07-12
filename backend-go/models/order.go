package models

import "time"

type OrderStatus struct {
}

type Order struct {
	IDUser  string
	IDStore string
	IDPromo string
	Date    time.Time
	Status  string
}
