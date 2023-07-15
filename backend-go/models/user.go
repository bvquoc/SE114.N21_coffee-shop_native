package models

import "coffee_shop_backend/constants"

type User struct {
	ID           string `json:"id"`
	Email        string `json:"email"`
	Name         string `json:"name"`
	AvatarURL    string `json:"avatarURL"`
	CoverURL     string `json:"coverURL"`
	IsSuperAdmin bool   `json:"isSuperAdmin"`
	IsAdmin      bool   `json:"isAdmin"`
	IsStaff      bool   `json:"isStaff"`
	StaffOf      string `json:"staffOf"`
	Role         string `json:"role"`
}

func NewUser(userInfo map[string]interface{}) *User {
	user := &User{
		ID:           "",
		Email:        "",
		AvatarURL:    constants.AVATAR_URL,
		CoverURL:     constants.COVER_URL,
		Name:         constants.DEFAULT_USER_NAME,
		IsSuperAdmin: false,
		IsAdmin:      false,
		IsStaff:      false,
		StaffOf:      "",
	}

	fields := map[string]*string{
		"email":     &user.Email,
		"avatarURL": &user.AvatarURL,
		"coverURL":  &user.CoverURL,
		"name":      &user.Name,
		"staffOf":   &user.StaffOf,
		"id":        &user.ID,
	}

	boolFields := map[string]*bool{
		"isSuperAdmin": &user.IsSuperAdmin,
		"isAdmin":      &user.IsAdmin,
		"isStaff":      &user.IsStaff,
	}

	for key, value := range userInfo {
		if str, ok := fields[key]; ok {
			if strValue, ok := value.(string); ok {
				*str = strValue
			}
		} else if boolean, ok := boolFields[key]; ok {
			if boolValue, ok := value.(bool); ok {
				*boolean = boolValue
			}
		}
	}

	return user
}
