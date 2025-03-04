package com.flywise.service;

import java.util.List;

import com.flywise.dto.AppUserDto;
import com.flywise.dto.CustomDto;
import com.flywise.exception.UserException;
import com.flywise.pojos.AppUser;
import com.flywise.pojos.Booking;

public interface IAppUserService {
	
	public List<AppUser> fetchAllUsers();
	
	void registerUser(AppUserDto appUserDto) throws UserException;
	 
	CustomDto confirmBooking(int bid);
	
	List<Booking> pastBooking(int uid);
}
