package com.flywise.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

	    private String token;
	    
	    private int userId;
	    
	    private String role;
	    
	    private String firstName;
}