package net.lermex.inaction.home;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.io.ByteStreams;

import net.lermex.inaction.dao.SportActivityDao;
import net.lermex.inaction.dao.UserActivityDao;
import net.lermex.inaction.dao.UserActivityShowDao;
import net.lermex.inaction.dao.UserAvatarShowDao;
import net.lermex.inaction.dao.UserDao;
import net.lermex.inaction.dao.UserStatusDao;
import net.lermex.inaction.entity.SportActivity;
import net.lermex.inaction.entity.User;
import net.lermex.inaction.entity.UserActivity;
import net.lermex.inaction.entity.UserActivityShow;
import net.lermex.inaction.entity.UserStatus;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class HomeController {
	
	@Autowired
	UserDao userDao;

	@Autowired
	SportActivityDao sportActivityDao;
	
	@Autowired
	UserStatusDao userStatusDao;
	
	@Autowired
	UserActivityDao userActivityDao;
	
	@Autowired
	UserActivityShowDao userActivityShowDao;
	
	@Autowired
	UserAvatarShowDao userAvatarShowDao;
	
	@Autowired
	ServletContext context;

	@RequestMapping(value = "/")
	public ModelAndView index() {
		List<Integer> listOfIntegers = Arrays.asList(4, 5, 6, 12, 3, 4, 5);
		final ModelAndView mav = new ModelAndView("home/homeNotSignedIn");
		List<Integer> activityMinutes = Arrays.asList(0, 2, 5, 3, 4, 5, 6);
		mav.addObject("customers", "test");
		mav.addObject("activityMinutes", activityMinutes);
		//build avatar url
		mav.addObject("avatarurl", "/resources/images/users/bike-person2.jpg");

		/*
		try {
			//create user
			User u = new User();
			u.setName("Pedro" + (Calendar.getInstance()).get(Calendar.MILLISECOND));
			u.setPassword("YESYOUARE");
			boolean createW = userDao.createUserIfNotExists(u);
			System.out.println(createW);
			//post msg
			UserStatus us = new UserStatus(u);
			us.setUserText("HI THERE "+(Calendar.getInstance()).get(Calendar.MILLISECOND));
			userStatusDao.createUserStatus(us);
			//create sport_activity
			SportActivity a = new SportActivity();
			a.setName("MixFight" +(Calendar.getInstance()).get(Calendar.MILLISECOND));
			sportActivityDao.createActivity(a);
			//create user activity
			UserActivity ua = new UserActivity(u, a);
			ua.setDt(new java.util.Date());
			ua.setMinutes(15);
			userActivityDao.createUserActivity(ua);			
		} catch (Exception ex) {
			//System.out.println(ex);
			ex.printStackTrace();
		}
		*/		
		User u = new User();		
		u.setName("Peter Sportsy");		
		mav.addObject("showuser", u);
		//show it
		List<UserActivityShow> listUserActivityShow = userActivityShowDao.getUserActivityShowList();
		mav.addObject("listUserActivityShow", listUserActivityShow);		
		
		return mav;
	}
		
	
	@RequestMapping(value = "/time")
	@ResponseBody
	public String getdata() {
		return new Date().toString();
	}
	
	@RequestMapping(value = "/dosome")
	@ResponseBody
	public String getdata(@RequestParam("myparam")String param) {
		return "hello "+param;
	}
	
	@RequestMapping(value = "/dosomeyet")
	@ResponseBody
	public String getanotherdata(@RequestBody String param, @RequestHeader("Content-Type") String contnttype) {
		System.out.println(contnttype);
		System.out.println(param);
		return "hello "+param;
	}
	
	/* Get user page */
	@RequestMapping(value = "/people/{userId}")
	public ModelAndView showuser(@PathVariable String userId) {
		List<Integer> listOfIntegers = Arrays.asList(4, 5, 6, 12, 3, 4, 5);
		final ModelAndView mav = new ModelAndView("home/homeNotSignedIn");
		List<Integer> activityMinutes = Arrays.asList(0, 2, 5, 3, 4, 5, 6);
		mav.addObject("activityMinutes", activityMinutes);
        //build avatar url
		mav.addObject("avatarurl", "/people/"+userId+"/avatar");		
		//show user by his Id	
		Long uId = Long.valueOf(userId);
		User u = userDao.getUserById(uId);
		if (u == null) { 
			u = new User();
			u.setId(uId); 
			u.setName("Who is this man?");
		}
		mav.addObject("showuser", u);
		//		
		List<UserActivityShow> listUserActivityShow = userActivityShowDao.getUserActivityShowList();
		mav.addObject("listUserActivityShow", listUserActivityShow);		
		
		return mav;
	}
	
	/* Get user avatar */	
	@RequestMapping(value = "/people/{userId}/avatar")
	public ResponseEntity<byte[]> getuseravatar(@PathVariable String userId) {
		final String noPhoto = "/resources/images/users/NoImageAvailable.png";
		final HttpHeaders headers = new HttpHeaders();
		byte[] result = userAvatarShowDao.getAvatarImageById(Long.valueOf(userId));
		if (result != null) {
			headers.setContentType(MediaType.IMAGE_JPEG);			
		} else {
			headers.setContentType(MediaType.IMAGE_PNG);	  
			try {
				result = ByteStreams.toByteArray(context.getResourceAsStream(noPhoto));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}	
		headers.setContentLength(result.length);
	    return new ResponseEntity<byte[]> (result, headers, HttpStatus.OK);
	}
}
