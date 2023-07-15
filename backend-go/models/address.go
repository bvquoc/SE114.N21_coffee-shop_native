package models

type Address struct {
	AddressNote      string  `json:"addressNote"`
	FormattedAddress string  `json:"formattedAddress"`
	Lat              float64 `json:"lat"`
	Lng              float64 `json:"lng"`
	NameReceiver     string  `json:"nameReceiver"`
	Phone            string  `json:"phone"`
}
