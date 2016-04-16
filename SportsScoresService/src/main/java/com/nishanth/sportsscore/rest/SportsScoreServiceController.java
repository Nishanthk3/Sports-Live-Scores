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
import org.springframework.web.bind.annotation.RequestParam;
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
	private String CODE;
	@Autowired
	private String local;
	@Autowired
	private String publicUrl;
	@Autowired
	private String developersExtension;
	@Autowired
	private String publicExtension;
	
	@Autowired
	private String extensionVersion;
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
	@Autowired
	private String bundesliga_football;
	@Autowired
	private String bundesliga_football_videos;
	@Autowired
	private String facup_football_videos;
	@Autowired
	private String laliga_football_videos;
	
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
	
	@RequestMapping( value="cricket/code",method = RequestMethod.GET)
	public @ResponseBody String cricketCode(@RequestParam("code") String code, ModelMap model, HttpServletRequest httpReq, HttpServletResponse httpResp)
	{
		if(code.equalsIgnoreCase(CODE))
			return cricket(code, model, httpReq, httpResp);
		return null;
	}
	@RequestMapping( value="cricket",method = RequestMethod.GET)
	public @ResponseBody String cricket(String code, ModelMap model, HttpServletRequest httpReq, HttpServletResponse httpResp)
	{
		if(checkDomain(httpReq,code) == false)
			return null;
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
		str=str.replace("&", "&amp;");
		
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

	@RequestMapping( value="cricket/live/code",method = RequestMethod.GET)
	public @ResponseBody List<List<Item>> liveCricketCode(@RequestParam("code") String code, ModelMap model, HttpServletRequest httpReq, HttpServletResponse httpResp)
	{
		if(code.equalsIgnoreCase(CODE))
			return liveCricket(code, model, httpReq, httpResp);
		return null;
	}
	@RequestMapping( value="cricket/live",method = RequestMethod.GET)
	public @ResponseBody List<List<Item>> liveCricket(String code, ModelMap model, HttpServletRequest httpReq, HttpServletResponse httpResp)
	{
		if(checkDomain(httpReq,code) == false)
			return null;
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
	
	@RequestMapping( value="cricket/rss/code",method = RequestMethod.GET)
	public @ResponseBody List<List<Item>> cricketRssCode(@RequestParam("code") String code, ModelMap model, HttpServletRequest httpReq, HttpServletResponse httpResp)
	{
		if(code.equalsIgnoreCase(CODE))
			return cricketRss(code, model, httpReq, httpResp);
		return null;
	}
	@RequestMapping( value="cricket/rss",method = RequestMethod.GET)
	public @ResponseBody List<List<Item>> cricketRss(String code, ModelMap model, HttpServletRequest httpReq, HttpServletResponse httpResp)
	{
		if(checkDomain(httpReq,code) == false)
			return null;
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
		str=str.replace("&", "&amp;");
		
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
		
		list = liveCricket(code, model, httpReq, httpResp);
		
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
		/** If there are no live international matches regional matches matches and premier leagues matched will be displayed on the top view in the UI*/
		if(list.get(0).size() == 0)
		{
			res.add(list1); //regional matches
			res.add(list.get(0)); // live international matches
		}
		else
		{
			res.add(list.get(0));// live international matches
			res.add(list1); //regional matches
		}
		res.add(list.get(1)); //news
		
		return res;
	}

	@RequestMapping( value="eplfootball/rss/code",method = RequestMethod.GET)
	public @ResponseBody List<List<Item>> eplFootballRssCode(@RequestParam("code") String code, ModelMap model, HttpServletRequest httpReq, HttpServletResponse httpResp)
	{
		if(code.equalsIgnoreCase(CODE))
			return eplFootballRss(code, model, httpReq, httpResp);
		return null;
	}
	@RequestMapping( value="eplfootball/rss",method = RequestMethod.GET)
	public @ResponseBody List<List<Item>> eplFootballRss(String code, ModelMap model, HttpServletRequest httpReq, HttpServletResponse httpResp)
	{
		if(checkDomain(httpReq,code) == false)
			return null;
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
		str=str.replace("&", "&amp;");
		
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
		
		list = eplFootballVideosRss(code, model, httpReq, httpResp);
		
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

	@RequestMapping( value="eplfootball/videos/rss/code",method = RequestMethod.GET)
	public @ResponseBody List<Item> eplFootballVideosRssCode(@RequestParam("code") String code, ModelMap model, HttpServletRequest httpReq, HttpServletResponse httpResp)
	{
		if(code.equalsIgnoreCase(CODE))
			return eplFootballVideosRss(code, model, httpReq, httpResp);
		return null;
	}
	@RequestMapping( value="eplfootball/videos/rss",method = RequestMethod.GET)
	public @ResponseBody List<Item> eplFootballVideosRss(String code, ModelMap model, HttpServletRequest httpReq, HttpServletResponse httpResp)
	{
		if(checkDomain(httpReq,code) == false)
			return null;
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
		str=str.replace("&", "&amp;");
		
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
			if(i.getDescription().contains("&lt;img style="))
			{
				i.setDescription(i.getDescription().split("/&gt;")[1]);
			}
			list.add(i);
		}
		return list;
	}
	
	@RequestMapping( value="uefachampsfootball/rss/code",method = RequestMethod.GET)
	public @ResponseBody List<List<Item>> champsFootballRssCode(@RequestParam("code") String code, ModelMap model, HttpServletRequest httpReq, HttpServletResponse httpResp)
	{
		if(code.equalsIgnoreCase(CODE))
			return champsFootballRss(code, model, httpReq, httpResp);
		return null;
	}
	@RequestMapping( value="uefachampsfootball/rss",method = RequestMethod.GET)
	public @ResponseBody List<List<Item>> champsFootballRss(String code, ModelMap model, HttpServletRequest httpReq, HttpServletResponse httpResp)
	{
		if(checkDomain(httpReq,code) == false)
			return null;
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
		str=str.replace("&", "&amp;");
		
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
		
		list = uefaChampsFootballVideosRss(code, model, httpReq, httpResp);
		
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
	
	@RequestMapping( value="uefachampsfootball/videos/rss/code",method = RequestMethod.GET)
	public @ResponseBody List<Item> uefaChampsFootballVideosRssCode(@RequestParam("code") String code, ModelMap model, HttpServletRequest httpReq, HttpServletResponse httpResp)
	{
		if(code.equalsIgnoreCase(CODE))
			return uefaChampsFootballVideosRss(code, model, httpReq, httpResp);
		return null;
	}
	@RequestMapping( value="uefachampsfootball/videos/rss",method = RequestMethod.GET)
	public @ResponseBody List<Item> uefaChampsFootballVideosRss(String code, ModelMap model, HttpServletRequest httpReq, HttpServletResponse httpResp)
	{
		if(checkDomain(httpReq,code) == false)
			return null;
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
		str=str.replace("&", "&amp;");
		
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
			if(i.getDescription().contains("&lt;img style="))
			{
				i.setDescription(i.getDescription().split("/&gt;")[1]);
			}
			list.add(i);
		}
		return list;
	}
	
	@RequestMapping( value="bundesligafootball/rss/code",method = RequestMethod.GET)
	public @ResponseBody List<List<Item>> bundesligaFootballRssCode(@RequestParam("code") String code, ModelMap model, HttpServletRequest httpReq, HttpServletResponse httpResp)
	{
		if(code.equalsIgnoreCase(CODE))
			return bundesligaFootballRss(code, model, httpReq, httpResp);
		return null;
	}
	@RequestMapping( value="bundesligafootball/rss",method = RequestMethod.GET)
	public @ResponseBody List<List<Item>> bundesligaFootballRss(String code, ModelMap model, HttpServletRequest httpReq, HttpServletResponse httpResp)
	{
		if(checkDomain(httpReq,code) == false)
			return null;
		httpResp.setHeader("Access-Control-Allow-Origin","*");
		Client client = Client.create();
		WebResource webResource = client.resource(bundesliga_football);
		ClientResponse response = webResource.get(ClientResponse.class);

		if (response.getStatus() != 200) {
			System.out.println("Response = "+response.getStatus());
			System.out.println("Content  = "+response.getEntity(String.class));
			throw new RuntimeException("Failed : HTTP error code : "+ response.getStatus());
		}
		String str = response.getEntity(String.class);
		str=str.replace("&", "&amp;");
		
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
		List<List<Item>> res = new ArrayList<List<Item>>();
		
		list = bundesligaFootballVideosRss(code, model, httpReq, httpResp);
		
		res.add(list);
		res.add(item);
		return res;
	}
	
	@RequestMapping( value="bundesligafootball/videos/rss/code",method = RequestMethod.GET)
	public @ResponseBody List<Item> bundesligaFootballVideosRssCode(@RequestParam("code") String code, ModelMap model, HttpServletRequest httpReq, HttpServletResponse httpResp)
	{
		if(code.equalsIgnoreCase(CODE))
			return bundesligaFootballVideosRss(code, model, httpReq, httpResp);
		return null;
	}
	@RequestMapping( value="bundesligafootball/videos/rss",method = RequestMethod.GET)
	public @ResponseBody List<Item> bundesligaFootballVideosRss(String code, ModelMap model, HttpServletRequest httpReq, HttpServletResponse httpResp)
	{
		if(checkDomain(httpReq,code) == false)
			return null;
		httpResp.setHeader("Access-Control-Allow-Origin","*");
		Client client = Client.create();
		WebResource webResource = client.resource(bundesliga_football_videos);
		ClientResponse response = webResource.get(ClientResponse.class);

		if (response.getStatus() != 200) {
			System.out.println("Response = "+response.getStatus());
			System.out.println("Content  = "+response.getEntity(String.class));
			throw new RuntimeException("Failed : HTTP error code : "+ response.getStatus());
		}
		String str = response.getEntity(String.class);
		str=str.replace("&", "&amp;");
		
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
			if(i.getDescription().contains("&lt;img style="))
			{
				i.setDescription(i.getDescription().split("/&gt;")[1]);
			}
			list.add(i);
		}
		return list;
	}
	
	@RequestMapping( value="facupfootball/rss/code",method = RequestMethod.GET)
	public @ResponseBody List<Item> facupfootballVideosRssCode(@RequestParam("code") String code, ModelMap model, HttpServletRequest httpReq, HttpServletResponse httpResp)
	{
		if(code.equalsIgnoreCase(CODE))
			return facupfootballVideosRss(code, model, httpReq, httpResp);
		return null;
	}
	@RequestMapping( value="facupfootball/rss",method = RequestMethod.GET)
	public @ResponseBody List<Item> facupfootballVideosRss(String code, ModelMap model, HttpServletRequest httpReq, HttpServletResponse httpResp)
	{
		if(checkDomain(httpReq,code) == false)
			return null;
		httpResp.setHeader("Access-Control-Allow-Origin","*");
		Client client = Client.create();
		WebResource webResource = client.resource(facup_football_videos);
		ClientResponse response = webResource.get(ClientResponse.class);

		if (response.getStatus() != 200) {
			System.out.println("Response = "+response.getStatus());
			System.out.println("Content  = "+response.getEntity(String.class));
			throw new RuntimeException("Failed : HTTP error code : "+ response.getStatus());
		}
		String str = response.getEntity(String.class);
		str=str.replace("&", "&amp;");
		
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
			if(i.getDescription().contains("&lt;img style="))
			{
				i.setDescription(i.getDescription().split("/&gt;")[1]);
			}
			list.add(i);
		}
		return list;
	}
	
	@RequestMapping( value="laligafootball/rss/code",method = RequestMethod.GET)
	public @ResponseBody List<Item> laligaFootballVideosRssCode(@RequestParam("code") String code, ModelMap model, HttpServletRequest httpReq, HttpServletResponse httpResp)
	{
		if(code.equalsIgnoreCase(CODE))
			return laligaFootballVideosRss(code, model, httpReq, httpResp);
		return null;
	}
	@RequestMapping( value="laligafootball/rss",method = RequestMethod.GET)
	public @ResponseBody List<Item> laligaFootballVideosRss(String code, ModelMap model, HttpServletRequest httpReq, HttpServletResponse httpResp)
	{
		if(checkDomain(httpReq,code) == false)
			return null;
		httpResp.setHeader("Access-Control-Allow-Origin","*");
		Client client = Client.create();
		WebResource webResource = client.resource(laliga_football_videos);
		ClientResponse response = webResource.get(ClientResponse.class);

		if (response.getStatus() != 200) {
			System.out.println("Response = "+response.getStatus());
			System.out.println("Content  = "+response.getEntity(String.class));
			throw new RuntimeException("Failed : HTTP error code : "+ response.getStatus());
		}
		String str = response.getEntity(String.class);
		str=str.replace("&", "&amp;");
		
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
			if(i.getDescription().contains("&lt;img style="))
			{
				i.setDescription(i.getDescription().split("/&gt;")[1]);
			}
			list.add(i);
		}
		return list;
	}
	@RequestMapping( value="extensionVersion",method = RequestMethod.GET)
	public @ResponseBody String extensionVersion(String code, ModelMap model, HttpServletRequest httpReq, HttpServletResponse httpResp)
	{
		httpResp.setHeader("Access-Control-Allow-Origin","*");
		return extensionVersion;
	}
	
	private boolean checkDomain(HttpServletRequest httpReq, String code)
	{
		if(httpReq.getHeader("Origin") == null)
		{
			if(!httpReq.getLocalName().equalsIgnoreCase(local))
			{
				if(code == null)
					return false;
				if(httpReq.getLocalName().equalsIgnoreCase(publicUrl) && !code.equalsIgnoreCase(CODE))
					return false;
			}
		}
		else if(httpReq.getHeader("Origin").equalsIgnoreCase(developersExtension)){}
		else if(httpReq.getHeader("Origin").equalsIgnoreCase(publicExtension)){}
		else
		{
			return false;
		}
		return true;
	}
	@SuppressWarnings("rawtypes")
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
