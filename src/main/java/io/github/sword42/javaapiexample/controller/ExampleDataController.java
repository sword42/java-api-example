/**
 * 
 * The MIT License (MIT)
 * 
 * Copyright (c) 2012 Shane Word
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.github.sword42.javaapiexample.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import io.github.sword42.javaapiexample.model.ExampleData;
import io.github.sword42.javaapiexample.service.IExampleDataService;

import java.util.HashMap;
import java.util.Random;

import javax.servlet.http.HttpServletResponse;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.ServletWebRequest;

/**
 *
 */
@Controller
@RequestMapping("/v1/exampledata")
public class ExampleDataController {
	private final static Logger logger = LoggerFactory.getLogger(ExampleDataController.class);

	public static final String FIELD_DATA = "data";
	public static final String FIELD_ERROR = "error";
	public static final String FIELD_STATUS_CODE = "statusCode";
	public static final String FIELD_ERROR_MESSAGE = "statusCode";

	protected IExampleDataService exampleDataService;

	@RequestMapping(value="{id}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ModelMap getExampleDataInJSON(@PathVariable String id, ServletWebRequest request) {
		logger.debug("received getExampleDataInJSON request for {} in request {}", id, request);
		ModelMap returnValue = new ModelMap();
		try {
			ExampleData data = exampleDataService.get(id);
			returnValue.addAttribute(FIELD_DATA, data);
		} catch (Exception e) {
			String message = "bad get user request for: "+id;
			logger.error(message);
			HashMap<String, String> errorMap = new HashMap<String, String>();
			returnValue.addAttribute(FIELD_ERROR, errorMap);
			errorMap.put(FIELD_STATUS_CODE, Integer.toString(HttpServletResponse.SC_BAD_REQUEST));
			errorMap.put(FIELD_ERROR_MESSAGE, message);
			request.getResponse().setStatus(HttpServletResponse .SC_BAD_REQUEST);
		}
		return returnValue;
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleException(Exception ex, ServletWebRequest request) {
		String message = "Exception: " + ex.getLocalizedMessage();
		logger.error("handleException {}", ex.getLocalizedMessage());
		logger.error("request {}", request.toString());
		HttpStatus status = HttpStatus.BAD_REQUEST;
		return new ResponseEntity<String>(message, status);
	}

	/**
	 * @param exampleDataService the exampleDataService to set
	 */
	public void setExampleDataService(IExampleDataService exampleDataService) {
		this.exampleDataService = exampleDataService;
	}

	@RunWith(SpringJUnit4ClassRunner.class)
	@ContextConfiguration
	public static class TestUnitTest {

		@Autowired
		private ExampleDataController exampleDataController;
		private Random theRandom = new Random();

		@Test
		public void testGetExampleDataWithId() throws Exception {
			String methodName = "testGetExampleDataWithId";
			IExampleDataService exampleDataService = Mockito.mock(IExampleDataService.class);
			exampleDataController.setExampleDataService(exampleDataService);

			ExampleData testData = ExampleData.create(methodName+theRandom.nextInt(),
					methodName+theRandom.nextInt(), methodName+theRandom.nextInt());

			Mockito.when(exampleDataService.get(Mockito.anyString())).thenReturn(testData);

			MockMvc mockMvc = MockMvcBuilders.standaloneSetup(this.exampleDataController).build();
			mockMvc.perform(get("/v1/exampledata/"+testData.id))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json"))
			.andExpect(jsonPath("$.data.id", Matchers.is(testData.id)))
			.andExpect(jsonPath("$.data.key", Matchers.is(testData.key)))
			.andExpect(jsonPath("$.data.value", Matchers.is(testData.value)))
			;
		}

		@Configuration
		static class TestExampleDataControllerConfiguration {
			@Bean
			public ExampleDataController exampleDataController() {
				return new ExampleDataController();
			}
		}
	}
}
