package com.trade.common.infrastructure.util.xml2json;

/*
	Copyright 2016 Arnaud Guyon

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 */

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Matcher;

/**
 * Converts XML to JSON
 */

public class XmlToJson {

	private static final String TAG = "XmlToJson";
	private static final String DEFAULT_CONTENT_NAME = "content";
	private static final String DEFAULT_ENCODING = "utf-8";
	private static final String DEFAULT_INDENTATION = "   ";
	private String mIndentationPattern = DEFAULT_INDENTATION;

	// default values when a Tag is empty
	private static final String DEFAULT_EMPTY_STRING = "";
	private static final int DEFAULT_EMPTY_INTEGER = 0;
	private static final long DEFAULT_EMPTY_LONG = 0;
	private static final double DEFAULT_EMPTY_DOUBLE = 0;
	private static final boolean DEFAULT_EMPTY_BOOLEAN = false;

	/**
	 * Builder class to create a XmlToJson object
	 */
	public static class Builder {

		private StringReader mStringSource;
		private InputStream mInputStreamSource;
		private String mInputEncoding = DEFAULT_ENCODING;
		private HashSet<String> mForceListPaths = new HashSet<>();
		private HashMap<String, String> mAttributeNameReplacements = new HashMap<>();
		private HashMap<String, String> mContentNameReplacements = new HashMap<>();
		private HashMap<String, Class> mForceClassForPath = new HashMap<>();    // Integer, Long, Double, Boolean
		private HashSet<String> mSkippedAttributes = new HashSet<>();
		private HashSet<String> mSkippedTags = new HashSet<>();

		/**
		 * Constructor
		 *
		 * @param xmlSource XML source
		 */
		public Builder(String xmlSource) {
			mStringSource = new StringReader(xmlSource);
		}

		/**
		 * Constructor
		 *
		 * @param inputStreamSource XML source
		 * @param inputEncoding     XML encoding format, can be null (uses UTF-8 if null).
		 */
		public Builder(InputStream inputStreamSource, String inputEncoding) {
			mInputStreamSource = inputStreamSource;
			mInputEncoding = (inputEncoding != null) ? inputEncoding : DEFAULT_ENCODING;
		}

		/**
		 * Force a XML Tag to be interpreted as a list
		 *
		 * @param path Path for the tag, with format like "/parentTag/childTag/tagAsAList"
		 * @return the Builder
		 */
		public Builder forceList(String path) {
			mForceListPaths.add(path);
			return this;
		}

		/**
		 * Change the name of an attribute
		 *
		 * @param attributePath   Path for the attribute, using format like "/parentTag/childTag/childTagAttribute"
		 * @param replacementName Name used for replacement (childTagAttribute becomes replacementName)
		 * @return the Builder
		 */
		public Builder setAttributeName(String attributePath, String replacementName) {
			mAttributeNameReplacements.put(attributePath, replacementName);
			return this;
		}

		/**
		 * Change the name of the key for a XML content
		 * In XML there is no extra key name for a tag content. So a default name "content" is used.
		 * This "content" name can be replaced with a custom name.
		 *
		 * @param contentPath     Path for the Tag that holds the content, using format like "/parentTag/childTag"
		 * @param replacementName Name used in place of the default "content" key
		 * @return the Builder
		 */
		public Builder setContentName(String contentPath, String replacementName) {
			mContentNameReplacements.put(contentPath, replacementName);
			return this;
		}

		/**
		 * Force an attribute or content value to be a INTEGER. A default value is used if the content is missing.
		 *
		 * @param path Path for the Tag content or Attribute, using format like "/parentTag/childTag"
		 * @return the Builder
		 */
		public Builder forceIntegerForPath(String path) {
			mForceClassForPath.put(path, Integer.class);
			return this;
		}

		/**
		 * Force an attribute or content value to be a LONG. A default value is used if the content is missing.
		 *
		 * @param path Path for the Tag content or Attribute, using format like "/parentTag/childTag"
		 * @return the Builder
		 */
		public Builder forceLongForPath(String path) {
			mForceClassForPath.put(path, Long.class);
			return this;
		}

		/**
		 * Force an attribute or content value to be a DOUBLE. A default value is used if the content is missing.
		 *
		 * @param path Path for the Tag content or Attribute, using format like "/parentTag/childTag"
		 * @return the Builder
		 */
		public Builder forceDoubleForPath(String path) {
			mForceClassForPath.put(path, Double.class);
			return this;
		}

		/**
		 * Force an attribute or content value to be a BOOLEAN. A default value is used if the content is missing.
		 *
		 * @param path Path for the Tag content or Attribute, using format like "/parentTag/childTag"
		 * @return the Builder
		 */
		public Builder forceBooleanForPath(String path) {
			mForceClassForPath.put(path, Boolean.class);
			return this;
		}

		/**
		 * Skips a Tag (will not be present in the JSON)
		 *
		 * @param path Path for the Tag, using format like "/parentTag/childTag"
		 * @return the Builder
		 */
		public Builder skipTag(String path) {
			mSkippedTags.add(path);
			return this;
		}

		/**
		 * Skips an attribute (will not be present in the JSON)
		 *
		 * @param path Path for the Attribute, using format like "/parentTag/childTag/ChildTagAttribute"
		 * @return the Builder
		 */
		public Builder skipAttribute(String path) {
			mSkippedAttributes.add(path);
			return this;
		}

		/**
		 * Creates the XmlToJson object
		 *
		 * @return a XmlToJson instance
		 */
		public XmlToJson build() {
			return new XmlToJson(this);
		}
	}

	private StringReader mStringSource;
	private InputStream mInputStreamSource;
	private String mInputEncoding;
	private HashSet<String> mForceListPaths;
	private HashMap<String, String> mAttributeNameReplacements;
	private HashMap<String, String> mContentNameReplacements;
	private HashMap<String, Class> mForceClassForPath;
	private HashSet<String> mSkippedAttributes = new HashSet<>();
	private HashSet<String> mSkippedTags = new HashSet<>();
	private JSONObject mJsonObject; // Used for caching the result

	private XmlToJson(Builder builder) {
		mStringSource = builder.mStringSource;
		mInputStreamSource = builder.mInputStreamSource;
		mInputEncoding = builder.mInputEncoding;
		mForceListPaths = builder.mForceListPaths;
		mAttributeNameReplacements = builder.mAttributeNameReplacements;
		mContentNameReplacements = builder.mContentNameReplacements;
		mForceClassForPath = builder.mForceClassForPath;
		mSkippedAttributes = builder.mSkippedAttributes;
		mSkippedTags = builder.mSkippedTags;

		// Build now so that the InputStream can be closed just after
		mJsonObject = convertToJSONObject();
	}

	/**
	 * @return the JSONObject built from the XML
	 */
	public JSONObject toJson() {
		return mJsonObject;
	}

	private JSONObject convertToJSONObject() {
		try {
			Tag parentTag = new Tag("", "xml");

			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			// tags with namespace are taken as-is ("namespace:tagname")
			factory.setNamespaceAware(false);
			XmlPullParser xpp = factory.newPullParser();

			setInput(xpp);

			int eventType = xpp.getEventType();
			while (eventType != XmlPullParser.START_DOCUMENT) {
				eventType = xpp.next();
			}
			readTags(parentTag, xpp);

			unsetInput();

			return convertTagToJson(parentTag, false);
		} catch (XmlPullParserException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private void setInput(XmlPullParser xpp) {
		if (mStringSource != null) {
			try {
				xpp.setInput(mStringSource);
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			}
		} else {
			try {
				xpp.setInput(mInputStreamSource, mInputEncoding);
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			}
		}
	}

	private void unsetInput() {
		if (mStringSource != null) {
			mStringSource.close();
		}
		// else the InputStream has been given by the user, it is not our role to close it
	}

	private void readTags(Tag parent, XmlPullParser xpp) {
		try {
			int eventType;
			do {
				eventType = xpp.next();
				if (eventType == XmlPullParser.START_TAG) {
					String tagName = xpp.getName();
					String path = parent.getPath() + "/" + tagName;

					boolean skipTag = mSkippedTags.contains(path);

					Tag child = new Tag(path, tagName);
					if (!skipTag) {
						parent.addChild(child);
					}

					// Attributes are taken into account as key/values in the child
					int attrCount = xpp.getAttributeCount();
					for (int i = 0; i < attrCount; ++i) {
						String attrName = xpp.getAttributeName(i);
						String attrValue = xpp.getAttributeValue(i);
						String attrPath = parent.getPath() + "/" + child.getName() + "/" + attrName;

						// Skip Attributes
						if (mSkippedAttributes.contains(attrPath)) {
							continue;
						}

						attrName = getAttributeNameReplacement(attrPath, attrName);
						Tag attribute = new Tag(attrPath, attrName);
						attribute.setContent(attrValue);
						child.addChild(attribute);
					}

					readTags(child, xpp);
				} else if (eventType == XmlPullParser.TEXT) {
					String text = xpp.getText();
					parent.setContent(text);
				} else if (eventType == XmlPullParser.END_TAG) {
					return;
				} else if (eventType == XmlPullParser.END_DOCUMENT) {
					return;
				} else {
				}
			} while (eventType != XmlPullParser.END_DOCUMENT);
		} catch (XmlPullParserException | IOException | NullPointerException e) {
			e.printStackTrace();
		}
	}

	private JSONObject convertTagToJson(Tag tag, boolean isListElement) {
		JSONObject json = new JSONObject();

		// Content is injected as a key/value
		if (tag.getContent() != null) {
			String path = tag.getPath();
			String name = getContentNameReplacement(path, DEFAULT_CONTENT_NAME);
			putContent(path, json, name, tag.getContent());
		}


		HashMap<String, ArrayList<Tag>> groups = tag.getGroupedElements(); // groups by tag names so that we can detect lists or single elements
		for (ArrayList<Tag> group : groups.values()) {

			// element, or list of 1
			if (group.size() == 1) {
				Tag child = group.get(0);
				// list of 1
				if (isForcedList(child)) {
					JSONArray list = new JSONArray();
					list.add(convertTagToJson(child, true));
					String childrenNames = child.getName();
					json.put(childrenNames, list);
					// stand alone element
				} else {
					if (child.hasChildren()) {
						JSONObject jsonChild = convertTagToJson(child, false);
						json.put(child.getName(), jsonChild);
					} else {
						String path = child.getPath();
						putContent(path, json, child.getName(), child.getContent());
					}
				}
			} else {    // list
				JSONArray list = new JSONArray();
				for (Tag child : group) {
					list.add(convertTagToJson(child, true));
				}
				String childrenNames = group.get(0).getName();
				json.put(childrenNames, list);
			}
		}
		return json;

	}

	private void putContent(String path, JSONObject json, String tag, String content) {
		// checks if the user wants to force a class (Int, Double... for a given path)
		Class forcedClass = mForceClassForPath.get(path);
		// default behaviour, put it as a String
		if (forcedClass == null) {
			if (content == null) {
				content = DEFAULT_EMPTY_STRING;
			}
			json.put(tag, content);
		} else {
			if (forcedClass == Integer.class) {
				try {
					Integer number = Integer.parseInt(content);
					json.put(tag, number);
				} catch (NumberFormatException exception) {
					json.put(tag, DEFAULT_EMPTY_INTEGER);
				}
			} else if (forcedClass == Long.class) {
				try {
					Long number = Long.parseLong(content);
					json.put(tag, number);
				} catch (NumberFormatException exception) {
					json.put(tag, DEFAULT_EMPTY_LONG);
				}
			} else if (forcedClass == Double.class) {
				try {
					Double number = Double.parseDouble(content);
					json.put(tag, number);
				} catch (NumberFormatException exception) {
					json.put(tag, DEFAULT_EMPTY_DOUBLE);
				}
			} else if (forcedClass == Boolean.class) {
				if (content == null) {
					json.put(tag, DEFAULT_EMPTY_BOOLEAN);
				} else if (content.equalsIgnoreCase("true")) {
					json.put(tag, true);
				} else if (content.equalsIgnoreCase("false")) {
					json.put(tag, false);
				} else {
					json.put(tag, DEFAULT_EMPTY_BOOLEAN);
				}
			}
		}
	}

	private boolean isForcedList(Tag tag) {
		String path = tag.getPath();
		return mForceListPaths.contains(path);
	}

	private String getAttributeNameReplacement(String path, String defaultValue) {
		String result = mAttributeNameReplacements.get(path);
		if (result != null) {
			return result;
		}
		return defaultValue;
	}

	private String getContentNameReplacement(String path, String defaultValue) {
		String result = mContentNameReplacements.get(path);
		if (result != null) {
			return result;
		}
		return defaultValue;
	}

	@Override
	public String toString() {
		if (mJsonObject != null) {
			return mJsonObject.toString();
		}
		return null;
	}

	/**
	 * Format the Json with indentation and line breaks
	 *
	 * @param indentationPattern indentation to use, for example " " or "\t".
	 *                           if null, use the default 3 spaces indentation
	 * @return the formatted Json
	 */
	public String toFormattedString(String indentationPattern) {
		if (indentationPattern == null) {
			mIndentationPattern = DEFAULT_INDENTATION;
		} else {
			mIndentationPattern = indentationPattern;
		}
		return toFormattedString();
	}

	/**
	 * Format the Json with indentation and line breaks.
	 * Uses the last intendation pattern used, or the default one (3 spaces)
	 *
	 * @return the Builder
	 */
	public String toFormattedString() {
		if (mJsonObject != null) {
			String indent = "";
			StringBuilder builder = new StringBuilder();
			builder.append("{\n");
			format(mJsonObject, builder, indent);
			builder.append("}\n");
			return builder.toString();
		}
		return null;
	}

	private void format(JSONObject jsonObject, StringBuilder builder, String indent) {
		Iterator<String> keys = jsonObject.keySet().iterator();
		while (keys.hasNext()) {
			String key = keys.next();
			builder.append(indent);
			builder.append(mIndentationPattern);
			builder.append("\"");
			builder.append(key);
			builder.append("\": ");
			Object value = jsonObject.get(key);
			if (value instanceof JSONObject) {
				JSONObject child = (JSONObject) value;
				builder.append(indent);
				builder.append("{\n");
				format(child, builder, indent + mIndentationPattern);
				builder.append(indent);
				builder.append(mIndentationPattern);
				builder.append("}");
			} else if (value instanceof JSONArray) {
				JSONArray array = (JSONArray) value;
				formatArray(array, builder, indent + mIndentationPattern);
			} else {
				formatValue(value, builder);
			}
			if (keys.hasNext()) {
				builder.append(",\n");
			} else {
				builder.append("\n");
			}
		}
	}

	private void formatArray(JSONArray array, StringBuilder builder, String indent) {
		builder.append("[\n");

		for (int i = 0; i < array.size(); ++i) {
			Object element = array.get(i);
			if (element instanceof JSONObject) {
				JSONObject child = (JSONObject) element;
				builder.append(indent);
				builder.append(mIndentationPattern);
				builder.append("{\n");
				format(child, builder, indent + mIndentationPattern);
				builder.append(indent);
				builder.append(mIndentationPattern);
				builder.append("}");
			} else if (element instanceof JSONArray) {
				JSONArray child = (JSONArray) element;
				formatArray(child, builder, indent + mIndentationPattern);
			} else {
				formatValue(element, builder);
			}
			if (i < array.size() - 1) {
				builder.append(",");
			}
			builder.append("\n");
		}
		builder.append(indent);
		builder.append("]");
	}

	private void formatValue(Object value, StringBuilder builder) {
		if (value instanceof String) {
			String string = (String) value;

			// Escape special characters
			// escape backslash
			string = string.replaceAll("\\\\", "\\\\\\\\");
			// escape double quotes
			string = string.replaceAll("\"", Matcher.quoteReplacement("\\\""));
			// escape slash
			string = string.replaceAll("/", "\\\\/");
			// escape \n and \t
			string = string.replaceAll("\n", "\\\\n").replaceAll("\t", "\\\\t");
			// escape \r
			string = string.replaceAll("\r", "\\\\r");

			builder.append("\"");
			builder.append(string);
			builder.append("\"");
		} else if (value instanceof Long) {
			Long longValue = (Long) value;
			builder.append(longValue);
		} else if (value instanceof Integer) {
			Integer intValue = (Integer) value;
			builder.append(intValue);
		} else if (value instanceof Boolean) {
			Boolean bool = (Boolean) value;
			builder.append(bool);
		} else if (value instanceof Double) {
			Double db = (Double) value;
			builder.append(db);
		} else {
			builder.append(value.toString());
		}
	}

	public static void main(String[] args) {
		String xmlString = "<test><a>1</a><b>xml</b></test>";
		XmlToJson xml2Json = new XmlToJson.Builder(xmlString).build();
		JSONObject json = xml2Json.toJson();
		System.out.println(json.get("test"));
		System.out.println(json.getJSONObject("test").getInteger("a"));
		System.out.println(json.getJSONObject("test").getString("b"));
	}

}
