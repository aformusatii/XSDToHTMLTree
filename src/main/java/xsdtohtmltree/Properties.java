package xsdtohtmltree;

import java.io.File;

import org.apache.commons.lang3.ObjectUtils;

public final class Properties {
	
	private Properties() {
		// prohibit instantiation
	}
	
	public static void init() {		
		for (Keys key : Keys.values()) {
			key.setValue(ObjectUtils.defaultIfNull(System.getProperty(key.getName()), key.getValue()));
			System.out.println("=> " + key.getName() + " = " + new File(key.getValue()).getAbsolutePath());
		}
		
		File outputDirectory = new File(Keys.OUTPUT_DIRECTORY.getValue());
		if (!outputDirectory.exists()) {
		    outputDirectory.mkdirs();
		}
	}
	
	public static String getProp(Keys key) {
		return key.getValue();
	}
	
	public enum Keys {
		ROOT_XSD_FILE(
				"rootXSDFile", "xsd/sample_shiporder.xsd"),
		OUTPUT_DIRECTORY(
				"outputDirectory", "output"),
		HTML_TEMPALTE_FILE(
				"htmlTemplatePath", "template/template.html");
		
		private String name;
		private String value;
		
		private Keys(String name, String defaultValue) {
			this.name = name;
			this.value = defaultValue;
		}

		public String getName() {
			return name;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}

}
