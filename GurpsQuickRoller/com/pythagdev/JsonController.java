package com.pythagdev;

import javax.json.*;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class JsonController {
	private JsonObject jObj = null;
	private JsonReader jReader;
	private JsonArray jArray;
	
	public JsonController() {
		jObj = null;
		jReader = null;
		jArray = null;
	}
	// file input constructor
	public JsonController(String sFile) {
		jReader = null;
		jArray = null;
		InputStream inFile = null;
		try {
			inFile = new FileInputStream(sFile);
		} catch (FileNotFoundException e) {
			System.out.println("File "+sFile+" not found.");
		}
		jReader = Json.createReader(inFile);
		jObj = jReader.readObject();
		jReader.close();
	}
	
	public JsonObject get() {
		return jObj;
	}
	
	public void set(JsonObject j) {
		jObj = j;
	}
	
	public String getString(String sKey) {
		if (jObj == null) return null;
		return jObj.getString(sKey);
	}
	
	public int getInt(String sKey) {
		return jObj.getJsonNumber(sKey).intValue();
	}
	
	public void setArray(String sKey) {
		if (jObj == null) {
			jArray = null;
			return;
		} else {
			jArray = jObj.getJsonArray(sKey);
		}
	}
	
	public JsonArray getArray() {
		return jArray;
	}
	
	public String getString(int i, String sKey) {
		if (jArray == null) return null;
		return jArray.getJsonObject(i).getString(sKey);
	}
	
	public int getInt(int i, String sKey) {
		if (jArray == null) return Integer.MIN_VALUE;
		JsonObject j = jArray.getJsonObject(i);
		int n = j.getJsonNumber(sKey).intValue();
		return n;
	}
}
