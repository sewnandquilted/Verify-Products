package verify.product.csv;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.util.Set;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.LinkedHashSet;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

public class CheckProductTable {
	private static boolean morelines;
	private static String line;
	private static String[] tokens;
	private static Integer[] categories;
	private static Integer[] Fabrics = { 5, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 107, 108, 109, 110, 111, 112,
			113, 117, 118, 120, 121, 123, 124, 125, 126, 127, 128, 129, 131, 132, 133, 134, 135, 136, 137, 138, 140,
			141, 142, 143, 144, 145, 146, 147, 148, 149, 150, 151, 152, 153, 154, 155, 156, 157, 158, 159, 160, 161,
			163, 164, 165, 166, 167, 168, 169, 170, 171, 172, 173, 174, 175, 176, 177, 178, 179, 180, 181, 182, 183,
			184, 185, 186, 187, 188, 189, 190, 192, 193, 194, 196, 197, 198, 199, 200, 201, 202, 204, 205, 206, 207,
			208, 209, 211, 212, 213, 214, 215, 216, 217, 218, 219, 220, 221, 222, 223, 225, 226, 227, 228, 229, 230,
			231, 232, 233, 234, 235, 236, 237, 239, 240, 241, 242, 243, 244, 245, 246, 247, 248, 249, 251, 252, 253,
			254, 255, 256, 257, 258, 260, 261, 262, 263, 264, 265, 266, 267, 268, 269, 270, 271, 272, 273, 274, 275,
			276, 277, 278, 279, 280, 281, 282, 283, 284, 286, 287, 288, 289, 291, 292, 295, 296, 297, 299, 300, 366,
			367, 368, 371, 373, 374, 381, 385, 386, 387, 388, 389, 390, 391, 392, 393, 395, 396, 397, 398, 399, 400,
			401, 402, 404, 405, 406, 407, 408, 410, 413, 416, 417, 418, 419, 420, 421, 422, 423, 424, 425, 428, 429,
			430, 431, 432, 433, 434, 435, 437, 438, 439, 440, 441, 442, 444, 445, 447, 448, 449, 450, 451, 452, 453,
			454, 455, 456, 457, 459, 460, 493, 494, 495, 500, 501, 502, 504, 506, 507 };
	private static String regexSpace = "[ $]+";
	private static String errorMessages;
	private static String BadChar = " ";
	private static int needsUpdatingcount = 0;
	private static String prevInternalID = "";
	private static int internalIDIgnored = 0;
	private static int categoryIdEmpty;
	private static int deptCodeEmpty;
	private static int statusIsEmpty;
	private static int weightIsEmpty;
	private static int nonFabricMeasuredPerYard;
	private static int ProductTitleIsEmpty;

	public static void main(String[] args) throws ClassNotFoundException, IOException {

		processCSVfile(chooseCSVfile());
		System.out.println("Goodbye!");
	}// end main

	private static boolean productIsFabric(String categories) {
		if (categories.isEmpty())
			return (false);
//		System.out.println("Categories:  " + categories);
		Integer[] arrayTwo = { 0, 0, 0, 0, 0, 0, 0, 0 };
		String[] parts = categories.substring(4).split(",");
		for (int i = 0; i < parts.length; i++) {
			arrayTwo[i] = Integer.parseInt(parts[i]);
		}
		Set<Integer> fabricCategories = new LinkedHashSet<Integer>(Arrays.asList(Fabrics));
		Set<Integer> thisCategory = new LinkedHashSet<Integer>(Arrays.asList(arrayTwo));
		for (Integer thisProductCatgory : thisCategory) {
			if (!fabricCategories.add(thisProductCatgory)) {
//				System.out.println("This category is a fabric " + thisProductCatgory);
				return (true);
			} else {
//				System.out.println("This category is Not fabric " + thisProductCatgory);
				fabricCategories.remove(thisProductCatgory);
			}
		}
		return (false);
	}

	private static void processCSVfile(String fileIn) throws IOException {
		File file = new File("/Users/geoffn/Downloads/testOut.csv");
		Writer writer = new OutputStreamWriter(new FileOutputStream(file), Charset.forName("UTF-8")); //$NON-NLS-1$
		CSVPrinter printer = new CSVPrinter(writer, CSVFormat.EXCEL);
		// printer.println();
		boolean firstTimeThrough = true;
		int shortRecords = 0;
		Reader in = new FileReader(fileIn);
		Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(in);
		// String [] hh;
		// final String[] headerCopy = CSVFormat.getHeader();
		// CSVFormat.getHeader();
		for (CSVRecord record : records) {
			// if (firstTimeThrough) {
			// printer.print("Remove,Internal ID(Do Not Change),Category IDs
			// (Comma separate),"
			// + "Dept Code,Status,Product Title,Short Description,"
			// + "Unit of Measurement(each/per
			// yard),Availability(web/store/both),"
			// + "Unlimited Inventory(yes/no),Options,Assigned option values,"
			// + "Option ID(Do Not Change),sku,upc,Manufacturer Product Id,"
			// + "Alternate Lookups,Manufacturer Id,Preferred Vendor,Store
			// Location ID,"
			// + "Weight,Price,Sale Price,Wholesale Price,Website Price,Website
			// Sale Price,"
			// + "Re-Order Point,Re-Order Amount,Tax Code,Date Added");
			// printer.println();
			// } else
			// firstTimeThrough = false;
			if ((record.size() < 29)) {
				shortRecords++;
				// System.out.println(record);
				// System.out
				// .println("Internal ID(Do Not Change)" + " |" +
				// record.get("Internal ID(Do Not Change)") + "|");
				// System.out.println("Status" + " |" + record.get("Status") +
				// "|");
			} else {
				// Write Description to a new browser window
				if (record.get("Short Description").contains(BadChar)) {
					// displayInBrowser(record.get("Short Description"));
				}
				// System.out.println("record size is "+record.size());
				if (prevInternalID.equals(record.get("Internal ID(Do Not Change)"))) {
					internalIDIgnored++;
				} else {
//					System.out.println("prevInternalID is '" + prevInternalID + "'");
//					System.out.println("currInternalID is '" + record.get("Internal ID(Do Not Change)") + "'");
					prevInternalID = record.get("Internal ID(Do Not Change)");
					boolean needsUpdating = checkThisProduct(record.get("Internal ID(Do Not Change)"),
							record.get("Category IDs (Comma separate)"), record.get("Dept Code"), record.get("Status"),
							record.get("Product Title"), record.get("Short Description"),
							// record.get("Long Description"),
							record.get("Unit of Measurement(each/per yard)"),
							record.get("Availability(web/store/both)"), record.get(10),
							record.get("Unlimited Inventory(yes/no)"), record.get("Options"),
							record.get("Assigned option values"), record.get("Option ID(Do Not Change)"),
							record.get("sku"), record.get("upc"), record.get("Manufacturer Product Id"),
							record.get("Alternate Lookups"), record.get("Manufacturer Id"),
							record.get("Preferred Vendor"), record.get("Store Location ID"), record.get("Weight"),
							record.get("Price"), record.get("Sale Price"), record.get("Wholesale Price"),
							record.get("Website Price"), record.get("Website Sale Price"), record.get("Re-Order Point"),
							record.get("Re-Order Amount"), record.get("Tax Code"), record.get("Date Added"));
					if (needsUpdating) {
						needsUpdatingcount++;
						printer.print("=\"" + errorMessages + "\"");
						printer.printRecord(record);
					}
				}
			}
			;
		}
		System.out.println("=================================================================");
		System.out.println("number of short records was " + shortRecords);
		System.out.println("products to be updated is  " + needsUpdatingcount);
		System.out.println("Product Option lines ignored " + internalIDIgnored);
		System.out.println("categoryIdEmpty     : " + categoryIdEmpty);
		System.out.println("DeptCodeEmpty       : " + deptCodeEmpty);
		System.out.println("statusIsEmpty       : " + statusIsEmpty);
		System.out.println("weightIsEmpty       : " + weightIsEmpty);
		System.out.println("ProductTitleIsEmpty : " + ProductTitleIsEmpty);
		
		System.out.println("nonFabricMeasuredPerYard : " + nonFabricMeasuredPerYard);
		writer.close();
		System.exit(1);
	}

	private static void displayInBrowser(String inString) {
		// TODO Auto-generated method stub
		File file = new File("test.html");
		try {
			Files.write(file.toPath(), inString.getBytes());
			Desktop.getDesktop().browse(file.toURI());
			System.exit(1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}

	}

	private static boolean checkThisProduct(String InternalID, String CategoryIDs, String DeptCode, String Status,
			String ProductTitle, String ShortDescription,
			// String LongDescription,
			String UnitOfMeasurement, String Availability, String string10, String UnlimitedInventory, String Options,
			String AssignedOptionValues, String OptionID, String sku, String upc, String ManufacturerProductId,
			String AlternateLookups, String ManufacturerId, String PreferredVendor, String StoreLocationID,
			String Weight, String Price, String SalePrice, String WholesalePrice, String WebSitePrice,
			String WebSiteSalePrice, String ReOrderPoint, String ReOrderAmount, String TaxCode, String DateAdded) {
		// TODO 1. missing value of CategoryID, DeptCode, Status, ProductTitle,
		// (Short && Long Description), UnlimitedInventory, (sku && upc &&
		// ManufacturerID)
		// Weight, Price, DateAdded
		// 2. Category==Fabric and weight <> 0.2kg (or xx for widebacks)
		// 3. ShortDescription or LongDescription contains invalid characters
		// 4. UnitOfMeasurement == yard and Category <> Fabric
		// 5. UnitOfMeasurement <> yard and Category == Fabric
		errorMessages = " ";

		if (CategoryIDs.isEmpty()) {
			errorMessages = errorMessages + "CategoryIDs.isEmpty, ";
			categoryIdEmpty++;
		}
		if (DeptCode.isEmpty()) {
			errorMessages = errorMessages + "DeptCode.isEmpty, ";
			deptCodeEmpty++;
		}
		if (Status.isEmpty()) {
			errorMessages = errorMessages + "Status.isEmpty, ";
			statusIsEmpty++;
		}
		 if (ProductTitle.isEmpty()) {
		 errorMessages = errorMessages + "ProductTitle.isEmpty, ";
		 ProductTitleIsEmpty++;
		 }
		// if (UnlimitedInventory.isEmpty()) {
		// errorMessages = errorMessages + "UnlimitedInventory.isEmpty, ";
		// }
		if (Weight.isEmpty()) {
			errorMessages = errorMessages + "Weight.isEmpty, ";
			weightIsEmpty++;
		}
		if (ShortDescription.contains(BadChar)) {
			errorMessages = errorMessages + "ShortDescription contains badchar, ";
		}
		// if (LongDescription.contains(BadChar)){
		// errorMessages=errorMessages+"LongDescription contains badchar, ";
		// }
		if (productIsFabric(CategoryIDs) && !UnitOfMeasurement.contains("per yard")) {
			if (ProductTitle.contains("kit") || ProductTitle.contains("Fat Eighth") || ProductTitle.contains("pre cut")
					|| ProductTitle.contains("LC") || ProductTitle.contains("Layer Cake") || ProductTitle.contains("JR")
					|| ProductTitle.contains("Jelly Roll") || ProductTitle.contains("kit")

					|| ShortDescription.contains("kit") || ShortDescription.contains("JR")
					|| ShortDescription.contains("Jelly Roll") || ShortDescription.contains("LC")
					|| ShortDescription.contains("Layer Cake") || ShortDescription.contains("pre cut")) {
			} else
				errorMessages = errorMessages + "Fabric is not measured per yard, ";
		}
		if (!productIsFabric(CategoryIDs) && UnitOfMeasurement.contains("per yard")) {
			errorMessages = errorMessages + "non-Fabric is measured per yard, ";
			nonFabricMeasuredPerYard++;
		}
		if (errorMessages.length() == 1) {
			return false;
		} else {
			System.out.println(errorMessages);
			return true;
		}
	}

	private static String trim(String string) {
		int length = string.length() - 1;
		System.out.println(string.substring(2, length));
		return string.substring(2, length);
	}

	private static void getNextLine(BufferedReader br) {
		// prevLine = line;
		try {
			morelines = ((line = br.readLine()) != null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		tokens = line.split(regexSpace);
	}

	private static String chooseCSVfile() {
		return "/Users/geoffn/Downloads/4872-edit-products-48.csv";

	}
}// end FirstExample