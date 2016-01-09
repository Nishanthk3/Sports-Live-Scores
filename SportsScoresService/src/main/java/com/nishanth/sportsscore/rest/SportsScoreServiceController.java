package com.nishanth.sportsscore.rest;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import com.nishanth.sportsscore.api.Channel;
import com.nishanth.sportsscore.api.Espn;
import com.nishanth.sportsscore.api.Item;
import com.nishanth.sportsscore.api.Match;
import com.nishanth.sportsscore.api.News;
import com.nishanth.sportsscore.api.RSS;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

@Controller
@RequestMapping("/")
public class SportsScoreServiceController {
	
	@Autowired
	private String cricket;
	@Autowired
	private String live_cricket;
	@Autowired
	private String cricket_href;
	@Autowired
	private String cricket_news_href;
	@Autowired
	private String epl_football;
	@Autowired
	private String epl_football_videos;
	@Autowired
	private String uefaChamps_football;
	@Autowired
	private String uefaChamps_football_videos;
	
	static String[] teams = {"australia", "india", "south", "southafrica",
							"sri", "srilanka", "new", "newzealand", "bangladesh",
							"pakistan", "england", "west", "westindies","ireland", 
							"scotland", "zimbabwe", "afghanistan"};  
	
	@Value("${version}")
	private String version;
	
	static JAXBContext jaxbContext;
	static Unmarshaller unmarshaller = null;
	static ObjectMapper objectMapper = null;
	static
	{
		try {
			jaxbContext = JAXBContext.newInstance(RSS.class);
			unmarshaller = jaxbContext.createUnmarshaller();
			objectMapper = new ObjectMapper();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@RequestMapping( value="")
	public @ResponseBody String welcomePage()
	{
		return "Welcome to Sports Scores Service, Version : "+version;
	}
	
	@RequestMapping( value="cricket",method = RequestMethod.GET)
	public @ResponseBody String cricket(ModelMap model, HttpServletRequest httpReq, HttpServletResponse httpResp)
	{
		httpResp.setHeader("Access-Control-Allow-Origin","*");
		Client client = Client.create();
		WebResource webResource = client.resource(cricket);

		ClientResponse response = webResource.get(ClientResponse.class);

		if (response.getStatus() != 200) {
			System.out.println("Response = "+response.getStatus());
			System.out.println("Content  = "+response.getEntity(String.class));
			throw new RuntimeException("Failed : HTTP error code : "+ response.getStatus());
		}
		String str = response.getEntity(String.class);

		StringReader reader = new StringReader(str);
		RSS rss = null;
		try {
			rss = (RSS) unmarshaller.unmarshal(reader);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		Channel channel = rss.getChannel();
		List<Item> item  = channel.getItem();
		List<String> list = new ArrayList<String>();
		for(Item i : item)
		{
			list.add(i.getTitle());
		}
		return "CRICKET \n"+list.toString().split("\\[")[1].split("\\]")[0].replace(",", "\n");
	}
	
	@RequestMapping( value="cricket/live",method = RequestMethod.GET)
	public @ResponseBody List<List<Item>> liveCricket(ModelMap model, HttpServletRequest httpReq, HttpServletResponse httpResp)
	{
		httpResp.setHeader("Access-Control-Allow-Origin","*");
		Client client = Client.create();
		WebResource webResource = client.resource(live_cricket);

		ClientResponse response = webResource.get(ClientResponse.class);

		if (response.getStatus() != 200) {
			System.out.println("Response = "+response.getStatus());
			System.out.println("Content  = "+response.getEntity(String.class));
			throw new RuntimeException("Failed : HTTP error code : "+ response.getStatus());
		}
		String str = response.getEntity(String.class);
		Espn espn = null;
		try {
			espn = objectMapper.readValue(str,Espn.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<List<Item>> res = new ArrayList<List<Item>>();
		List<Item> scoresItem = new ArrayList<Item>();
		List<Item> newsItem = new ArrayList<Item>();
		List<Match> match = espn.getMatch();
		List<News> news = espn.getNews();
		if(match != null)
		{
			for(Match m : match)
			{
				Item it = new Item();
				it.setTitle(m.getTitle());
				it.setDescription(m.getDescription());
				it.setLink(cricket_href.replace("id", m.id));
				scoresItem.add(it);
			}
		}
		if(news != null)
		{
			for(News n : news)
			{
				Item it = new Item();
				it.setTitle(n.getT());
				it.setDescription(n.getS());
				it.setLink(cricket_news_href.replace("data", n.b).replace("id", n.id));
				newsItem.add(it);
			}
		}
		res.add(scoresItem);
		res.add(newsItem);
		return res;
	}
	
	@RequestMapping( value="cricket/rss",method = RequestMethod.GET)
	public @ResponseBody List<List<Item>> cricketRss(ModelMap model, HttpServletRequest httpReq, HttpServletResponse httpResp)
	{
		httpResp.setHeader("Access-Control-Allow-Origin","*");
		Client client = Client.create();
		WebResource webResource = client.resource(cricket);

		ClientResponse response = webResource.get(ClientResponse.class);

		if (response.getStatus() != 200) {
			System.out.println("Response = "+response.getStatus());
			System.out.println("Content  = "+response.getEntity(String.class));
			throw new RuntimeException("Failed : HTTP error code : "+ response.getStatus());
		}
		String str = response.getEntity(String.class);

		StringReader reader = new StringReader(str);
		RSS rss = null;
		try {
			rss = (RSS) unmarshaller.unmarshal(reader);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		Channel channel = rss.getChannel();
		List<Item> item  = channel.getItem();
		List<List<Item>> list = new ArrayList<List<Item>>();
		List<Item> list1 = new ArrayList<Item>();
		List<List<Item>> res = new ArrayList<List<Item>>();
		
		list = liveCricket(model, httpReq, httpResp);
		
		for(Item i : item)
		{
			String strArray[] = i.getTitle().split("v");
			String strSplitArray1[] = strArray[0].trim().split(" ");
			String strSplitArray2[] = strArray[1].trim().split(" ");
			
			if(!Arrays.asList(teams).contains(strSplitArray1[0].toLowerCase()) || !Arrays.asList(teams).contains(strSplitArray2[0].toLowerCase()))
			{
				list1.add(i);
			}
		}
		res.add(list.get(0));// live international matches
		res.add(list1); //regional matches
		res.add(list.get(1)); //news
		return res;
	}

	@RequestMapping( value="eplfootball/rss",method = RequestMethod.GET)
	public @ResponseBody List<List<Item>> eplFootballRss(ModelMap model, HttpServletRequest httpReq, HttpServletResponse httpResp)
	{
		httpResp.setHeader("Access-Control-Allow-Origin","*");
		Client client = Client.create();
		WebResource webResource = client.resource(epl_football);
		ClientResponse response = webResource.get(ClientResponse.class);

		if (response.getStatus() != 200) {
			System.out.println("Response = "+response.getStatus());
			System.out.println("Content  = "+response.getEntity(String.class));
			throw new RuntimeException("Failed : HTTP error code : "+ response.getStatus());
		}
		String str = response.getEntity(String.class);

		StringReader reader = new StringReader(str);
		RSS rss = null;
		try {
			rss = (RSS) unmarshaller.unmarshal(reader);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		Channel channel = rss.getChannel();
		List<Item> item  = channel.getItem();
		List<Item> list = new ArrayList<Item>();
		List<Item> list1 = new ArrayList<Item>();
		List<List<Item>> res = new ArrayList<List<Item>>();
		
		list = eplFootballVideosRss(model, httpReq, httpResp);
		
		for(Item i : item)
		{
			if(i.getTitle().toLowerCase().contains(": match report") || i.getTitle().toLowerCase().contains(": live"))
			{
				if(i.getDescription().contains("<br><br>"))
				{
					i.setDescription(i.getDescription().replace("<br><br>", ".\n"));
				}
				if(i.getDescription().contains("<br>"))
				{
					i.setDescription(i.getDescription().replace("<br>", ""));
				}
				list1.add(i);
			}
		}	
		res.add(list);
		res.add(list1);
		return res;
	}

	@RequestMapping( value="eplfootball/videos/rss",method = RequestMethod.GET)
	public @ResponseBody List<Item> eplFootballVideosRss(ModelMap model, HttpServletRequest httpReq, HttpServletResponse httpResp)
	{
		httpResp.setHeader("Access-Control-Allow-Origin","*");
		Client client = Client.create();
		WebResource webResource = client.resource(epl_football_videos);
		ClientResponse response = webResource.get(ClientResponse.class);

		if (response.getStatus() != 200) {
			System.out.println("Response = "+response.getStatus());
			System.out.println("Content  = "+response.getEntity(String.class));
			throw new RuntimeException("Failed : HTTP error code : "+ response.getStatus());
		}
		String str = response.getEntity(String.class);
		StringReader reader = new StringReader(str);
		RSS rss = null;
		try {
			rss = (RSS) unmarshaller.unmarshal(reader);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		Channel channel = rss.getChannel();
		List<Item> item  = channel.getItem();
		List<Item> list = new ArrayList<Item>();
		for(Item i : item)
		{
			if(i.getDescription().contains("<img style="))
			{
				i.setDescription(i.getDescription().split("/>")[1]);
			}
			list.add(i);
		}
		return list;
	}

	@RequestMapping( value="uefachampsfootball/rss",method = RequestMethod.GET)
	public @ResponseBody List<List<Item>> champsFootballRss(ModelMap model, HttpServletRequest httpReq, HttpServletResponse httpResp)
	{
		httpResp.setHeader("Access-Control-Allow-Origin","*");
		Client client = Client.create();
		WebResource webResource = client.resource(uefaChamps_football);
		ClientResponse response = webResource.get(ClientResponse.class);

		if (response.getStatus() != 200) {
			System.out.println("Response = "+response.getStatus());
			System.out.println("Content  = "+response.getEntity(String.class));
			throw new RuntimeException("Failed : HTTP error code : "+ response.getStatus());
		}
		String str = response.getEntity(String.class);

		StringReader reader = new StringReader(str);
		RSS rss = null;
		try {
			rss = (RSS) unmarshaller.unmarshal(reader);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		Channel channel = rss.getChannel();
		List<Item> item  = channel.getItem();
		List<Item> list = new ArrayList<Item>();
		List<Item> list1 = new ArrayList<Item>();
		List<List<Item>> res = new ArrayList<List<Item>>();
		
		list = uefaChampsFootballVideosRss(model, httpReq, httpResp);
		
		for(Item i : item)
		{
			if(i.getTitle().toLowerCase().contains(": match report") || i.getTitle().toLowerCase().contains(": live"))
			{
				if(i.getDescription().contains("<br><br>"))
				{
					i.setDescription(i.getDescription().replace("<br><br>", ".\n"));
				}
				if(i.getDescription().contains("<br>"))
				{
					i.setDescription(i.getDescription().replace("<br>", ""));
				}
				list1.add(i);
			}
		}
		res.add(list);
		res.add(list1);
		return res;
	}
	
	@RequestMapping( value="uefachampsfootball/videos/rss",method = RequestMethod.GET)
	public @ResponseBody List<Item> uefaChampsFootballVideosRss(ModelMap model, HttpServletRequest httpReq, HttpServletResponse httpResp)
	{
		httpResp.setHeader("Access-Control-Allow-Origin","*");
		Client client = Client.create();
		WebResource webResource = client.resource(uefaChamps_football_videos);
		ClientResponse response = webResource.get(ClientResponse.class);

		if (response.getStatus() != 200) {
			System.out.println("Response = "+response.getStatus());
			System.out.println("Content  = "+response.getEntity(String.class));
			throw new RuntimeException("Failed : HTTP error code : "+ response.getStatus());
		}
		String str = response.getEntity(String.class);
		StringReader reader = new StringReader(str);
		RSS rss = null;
		try {
			rss = (RSS) unmarshaller.unmarshal(reader);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		Channel channel = rss.getChannel();
		List<Item> item  = channel.getItem();
		List<Item> list = new ArrayList<Item>();
		for(Item i : item)
		{
			if(i.getDescription().contains("<img style="))
			{
				i.setDescription(i.getDescription().split("/>")[1]);
			}
			list.add(i);
		}
		return list;
	}
	
	@Autowired
	private void setRequestMappingHandlerAdapter(RequestMappingHandlerAdapter mappingManager) {
		if(mappingManager == null) {
			return;
		}
		for( HttpMessageConverter conv : mappingManager.getMessageConverters()) {
			if( conv instanceof MappingJacksonHttpMessageConverter) {
				( (MappingJacksonHttpMessageConverter) conv).setObjectMapper(new JacksonCustomObjectMapper());
			}
		}
	}

}
