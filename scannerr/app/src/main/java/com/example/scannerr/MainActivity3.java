package com.example.scannerr;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity3 extends AppCompatActivity {
    private static final int CAMERA_REQUEST_CODE = 100;
    private boolean isMenuOpen = false;
    private boolean isFirstTime = true;
    private GestureDetector gestureDetector;
    private LinearLayout footerLayout;
    private FrameLayout menuContainer;
    private ScrollView scrollView;
    private String[] options;
    private ArrayList<String> fieldsList;
    private List<String> extractedData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        FirebaseApp.initializeApp(this);

        // Changing the status bar color
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.dark_blue));

        // Initializations of options
        options = new String[]{
                getString(R.string.ORGANIZATION),
                getString(R.string.PHONE),
                getString(R.string.EMAIL),
                getString(R.string.WEBSITE),
                getString(R.string.REPRESENTATIVE),
                getString(R.string.OCCUPATION),
                getString(R.string.ADDRESS),
                getString(R.string.TELEPHONE),
                getString(R.string.MOBILE),
                getString(R.string.FAX)
        };
        fieldsList = new ArrayList<>(Arrays.asList(options));
//        Log.println(Log.DEBUG, "MyInfo", "The string is true: " + getString(R.string.WEBSITE));

        footerLayout = findViewById(R.id.footer);
        menuContainer = findViewById(R.id.menuContainer);
        scrollView = findViewById(R.id.scrollView);

        Button btnMore = findViewById(R.id.btnMore);
        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMenuOpen) {
                    closeMenu();
                } else {
                    openMenu();
                }
            }
        });

        LayoutInflater inflater = LayoutInflater.from(this);
        View menuView = inflater.inflate(R.layout.activity_menu_layout, menuContainer, false);

        Button aboutUsButton = menuView.findViewById(R.id.btnAboutUs);
        Button contactUsButton = menuView.findViewById(R.id.btnContactUs);
        Button helpButton = menuView.findViewById(R.id.btnHelp);
        Button goBackButton = menuView.findViewById(R.id.btnGoBack);

        aboutUsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity3.this, Help.class);
                startActivity(intent);
            }
        });

        contactUsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Add functionality for Contact Us
                Toast.makeText(MainActivity3.this, "No Data", Toast.LENGTH_SHORT).show();
            }
        });

        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Add functionality for Help
                Toast.makeText(MainActivity3.this, "No Data", Toast.LENGTH_SHORT).show();
            }
        });

        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeMenu();
            }
        });

        Button addFieldButton = findViewById(R.id.btnAddField);
        addFieldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity3.this);
                builder.setTitle("Select an Option");

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addFieldButtonAction(options[which], "");
                    }
                });
                builder.setIcon(R.drawable.editblue);
                builder.show();
            }
        });

        // Add the menu layout to the menu container
        menuContainer.addView(menuView);

        addFieldButtonAction(getString(R.string.ORGANIZATION), "");
        addFieldButtonAction(getString(R.string.PHONE), "");
        addFieldButtonAction(getString(R.string.EMAIL), "");
        addFieldButtonAction(getString(R.string.ADDRESS), "");

        if (isFirstTime) {
            new AlertDialog.Builder(MainActivity3.this)
                    .setTitle("Tip")
                    .setMessage("You can directly tap on the field name to change it")
                    .setPositiveButton("Got it", null)
                    .setIcon(R.drawable.idea)
                    .show();
        }

        Button cameraButton = findViewById(R.id.button);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout container = findViewById(R.id.linear_layout);
                if (!isFirstTime && container.getChildCount() > 0) {
                    new AlertDialog.Builder(MainActivity3.this)
                            .setTitle("Reset")
                            .setMessage("If you proceed, all the current data will be erased. Do you want to continue?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    extractedData.clear();
                                    fieldsList.clear();
                                    fieldsList.addAll(Arrays.asList(options));
                                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                                }
                            })
                            .setNegativeButton("No", null)
                            .setCancelable(true)
                            .setIcon(R.drawable.caution)
                            .show();
                }
                else {
                    extractedData.clear();
                    fieldsList.clear();
                    fieldsList.addAll(Arrays.asList(options));
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap photo = (Bitmap) extras.get("data");
                if (photo != null) {
                    // Use ML Kit to process the photo and extract text
                    processPhoto(photo);
                }
            }
        }
    }

    private void processPhoto(Bitmap photo) {
        isFirstTime = false;
        InputImage image = InputImage.fromBitmap(photo, 0);
        LinearLayout container = findViewById(R.id.linear_layout);

        TextRecognizer recognizer = TextRecognition.getClient();
        Task<Text> result = recognizer.process(image)
                .addOnSuccessListener(text -> {
                    // Removing all previous fields
                    container.removeAllViewsInLayout();
                    Log.println(Log.DEBUG, "MyInfo", "\nCleared . . .\n");

                    String extractedText = text.getText();
                    String[] lines = extractedText.split("\n");

                    // Check for keywords to delete
                    String[] obsolete = {"email", "website", "phone", "address", "organization", "name", "telephone", "fax", "mobile"};

                    List<String> relevantLines = new ArrayList<>();

                    // Filtering ...
                    for (String line : lines) {
                        String prettyLine = line.toLowerCase().trim();
                        boolean skipLine = false;

                        for (String keyword : obsolete) {
                            if (prettyLine.equals(keyword)) {
                                skipLine = true;
                                break;
                            }
                        }

                        if (!skipLine && line.length() > 2) {
                            relevantLines.add(line);
                            extractedData.add(line);
                        }
                    }

                    lines = relevantLines.toArray(new String[0]);

                    // Check each line for relevant information
                    for (int j = 0; j < lines.length; j++) {
                        String line = lines[j];
                        Log.println(Log.DEBUG, "MyInfo", "This line: " + line);

                        // Check if the line is a website
                        if (!line.contains("@") &&  (line.matches("(?i)^(www|blog|shop|ftp|http|ftps).*") || line.matches(".*\\.(com|org|net|edu|gov|io|pk)$"))) {
//                            Log.println(Log.DEBUG, "MyInfo", "The control is in website");
                            String prettyWebsite = line.replaceAll("\\s+", "");
                            prettyWebsite = prettyWebsite.toLowerCase();
                            extractedData.set(extractedData.indexOf(line), prettyWebsite);
                            if (fieldsList.contains(getString(R.string.WEBSITE))) {
                                fieldsList.remove(getString(R.string.WEBSITE));
                                addFieldButtonAction(getString(R.string.WEBSITE), prettyWebsite);
                                Log.println(Log.DEBUG, "MyInfo", "Website added");
                            }
                            else {
                                Log.d( "MyInfo", "Alternate website: " + prettyWebsite);
                            }
                        }

                        // Check for email address
                        else if (line.matches(".*@.*\\..*")) {
//                            Log.println(Log.DEBUG, "MyInfo", "The control is in email");
                            String prettyEmail = line.replaceAll("\\s+", "");
                            prettyEmail = prettyEmail.toLowerCase();
                            extractedData.set(extractedData.indexOf(line), prettyEmail);
                            if (fieldsList.contains(getString(R.string.EMAIL))) {
                                fieldsList.remove(getString(R.string.EMAIL));
                                addFieldButtonAction(getString(R.string.EMAIL), prettyEmail);
                                Log.println(Log.DEBUG, "MyInfo", "Email added");
                            }
                            else {
                                Log.d( "MyInfo", "Alternate email: " + prettyEmail);
                            }
                        }

                        // Check for phone number
                        else if (hasMoreDigits(line)) {
//                            Log.println(Log.DEBUG, "MyInfo", "The control is in phone");
                            String prettyPhone = line.trim();
                            extractedData.set(extractedData.indexOf(line), prettyPhone);
                            if (fieldsList.contains(getString(R.string.PHONE))) {
                                fieldsList.remove(getString(R.string.PHONE));
                                addFieldButtonAction(getString(R.string.PHONE), prettyPhone);
                                Log.println(Log.DEBUG, "MyInfo", "Phone added");
                            }
                            else {
                                Log.d( "MyInfo", "Alternate phone: " + prettyPhone);
                            }
                        }

                        // Check for occupation along with the representative name
                        else if (line.matches("^[a-zA-Z -]*$") && fieldsList.contains(getString(R.string.OCCUPATION))){
//                            Log.println(Log.DEBUG, "MyInfo", "The control is at start of occupation");
                            if (line.matches(".*\\b(CEO|CFO|CTO|HR|VP|GM)\\b.*")) {
//                                Log.println(Log.DEBUG, "MyInfo", "The control is ALPHABETS occupation");
                                // Check if the representative name is adjacent to occupation
                                if (j == 0 && isName(lines[j+1]) && fieldsList.contains(getString(R.string.REPRESENTATIVE))) {
                                    fieldsList.remove(getString(R.string.REPRESENTATIVE));
                                    addFieldButtonAction(getString(R.string.REPRESENTATIVE), lines[++j]);
                                    Log.println(Log.DEBUG, "MyInfo", "Name added");
                                }
                                else if (j > 0 && j < lines.length - 1) {
                                    if (isName(lines[j-1])  && fieldsList.contains(getString(R.string.REPRESENTATIVE))) {
                                        fieldsList.remove(getString(R.string.REPRESENTATIVE));
                                        addFieldButtonAction(getString(R.string.REPRESENTATIVE), lines[j-1]);
                                        deleteDuplicateOrg(container, lines[j-1]);
                                        Log.println(Log.DEBUG, "MyInfo", "Previous Name added");
                                    }
                                    else if (isName(lines[j+1])  && fieldsList.contains(getString(R.string.REPRESENTATIVE))) {
                                        fieldsList.remove(getString(R.string.REPRESENTATIVE));
                                        addFieldButtonAction(getString(R.string.REPRESENTATIVE), lines[++j]);
                                        Log.println(Log.DEBUG, "MyInfo", "Name added");
                                    }
                                }

                                // Contains abbreviated occupation
                                String prettyOccupation = line.trim();
                                extractedData.set(extractedData.indexOf(line), prettyOccupation);
                                if (fieldsList.contains(getString(R.string.OCCUPATION))) {
                                    fieldsList.remove(getString(R.string.OCCUPATION));
                                    addFieldButtonAction(getString(R.string.OCCUPATION), prettyOccupation);
                                    Log.println(Log.DEBUG, "MyInfo", "Occupation added part 1");
                                }
                                else {
                                    Log.d( "MyInfo", "Alternate occupation: " + prettyOccupation);
                                }
                            }
                            else {
                                Log.println(Log.DEBUG, "MyInfo", "The control is checking the keyword occupation");
                                String[] keywords = {"manager", "sales", "analyst", "developer", "designer",
                                        "coordinator", "director", "specialist", "engineer", "representative",
                                        "owner", "assistant", "writer", "cameraman", "co-ordinator", "founder",
                                        "administrator", "facilitator", "supervisor", "executor", "moderator",
                                        "photographer", "consultant", "lead", "co-founder"};
                                String lowercaseLine = line.toLowerCase();
                                lowercaseLine = lowercaseLine.trim();

                                boolean containsKeyword = false;
                                for (String keyword : keywords) {
                                    String pattern = "(?i)(?<=^|\\s)" + keyword.toLowerCase() + "(?=$|\\s)";
                                    if (lowercaseLine.matches(".*" + pattern + ".*")) {
                                        containsKeyword = true;
                                        break;
                                    }
                                }

                                if (containsKeyword) {
                                    Log.println(Log.DEBUG, "MyInfo", "Found keyword");
                                    Log.d("MyInfo", "j = " + j);
//                                    Log.d("MyInfo", "lines[j+1] = " + lines[j+1]);
//                                    Log.d("MyInfo", "lines[j-1] = " + lines[j-1]);
//                                    Log.d("MyInfo", "isName(lines[j-1]) = " + isName(lines[j-1]));

                                    if (lines.length > 1 && fieldsList.contains(getString(R.string.REPRESENTATIVE))) {
                                        // Check if the representative name is adjacent to occupation
                                        if (j == 0) {
                                            if (isName(lines[j + 1]) ) {
                                                fieldsList.remove(getString(R.string.REPRESENTATIVE));
                                                addFieldButtonAction(getString(R.string.REPRESENTATIVE), lines[++j]);
                                                Log.println(Log.DEBUG, "MyInfo", "Next name added");
                                            }
                                        }
                                        // If j is between first and last (exclusive)
                                        else if (j > 0 && j < lines.length - 1) {

                                            if (isName(lines[j - 1])) {
                                                fieldsList.remove(getString(R.string.REPRESENTATIVE));
                                                addFieldButtonAction(getString(R.string.REPRESENTATIVE), lines[j - 1]);
                                                deleteDuplicateOrg(container, lines[j - 1]);
                                                Log.println(Log.DEBUG, "MyInfo", "Previous Name added");
                                            }

                                            else if (isName(lines[j + 1])) {
                                                fieldsList.remove(getString(R.string.REPRESENTATIVE));
                                                addFieldButtonAction(getString(R.string.REPRESENTATIVE), lines[++j]);
                                                Log.println(Log.DEBUG, "MyInfo", "Next name added");
                                            }
                                        }
                                        else if (j == lines.length - 1 && isName(lines[j - 1])) {
                                            fieldsList.remove(getString(R.string.REPRESENTATIVE));
                                            addFieldButtonAction(getString(R.string.REPRESENTATIVE), lines[j - 1]);
                                            deleteDuplicateOrg(container, lines[j - 1]);
                                            Log.println(Log.DEBUG, "MyInfo", "Previous Name added");
                                        }
                                    }

                                    // The 'line' contains a keyword that matches a business card occupation
                                    // Prettifying ...
                                    String prettyOccupation = "";
                                    for (int i = 0; i < lowercaseLine.toCharArray().length; i++) {
                                        char ch = lowercaseLine.charAt(i);

                                        if (i == 0)
                                            prettyOccupation += Character.toUpperCase(ch);

                                        else if (ch == ' ') {
                                            prettyOccupation += " ";
                                            prettyOccupation += Character.toUpperCase(lowercaseLine.charAt(++i));
                                        } else
                                            prettyOccupation += Character.toLowerCase(lowercaseLine.charAt(i));
                                    }

                                    extractedData.set(extractedData.indexOf(line), prettyOccupation);
                                    if (fieldsList.contains(getString(R.string.OCCUPATION))) {
                                        fieldsList.remove(getString(R.string.OCCUPATION));
                                        addFieldButtonAction(getString(R.string.OCCUPATION), prettyOccupation);
                                        Log.println(Log.DEBUG, "MyInfo", "Occupation added part 2");
                                    }
                                    else {
                                        Log.d( "MyInfo", "Alternate occupation: " + prettyOccupation);
                                    }

                                }
                            }
                        }

                        // Checking for address
                        else {
//                            Log.println(Log.DEBUG, "MyInfo", "The control is in keyword");
                            String[] keywords = {"block", "sector", "road", "town", "street", "city",
                                    "house", "floor", "cantt", "avenue", "area", "colony", "state",
                                    "estate", "plot", "flat", "apartment", "near", "across from",
                                    "next to", "north", "south", "east", "west", "office"};

                            String lowercaseLine = line.toLowerCase();
                            lowercaseLine = lowercaseLine.trim();

                            boolean containsKeyword = false;
                            for (String keyword : keywords) {
                                String pattern = "(?i)(?<=^|\\s)" + keyword.toLowerCase() + "(?=$|\\s|[,.$])";
                                if (lowercaseLine.matches(".*" + pattern + ".*")) {
                                    containsKeyword = true;
//                                    Log.println(Log.DEBUG, "MyInfo", "The control contains address keyword");
                                    break;
                                }
                            }

                            if (containsKeyword) {
//                                Log.println(Log.DEBUG, "MyInfo", "The control is in address");
                                String prettyAddress = line.trim();
                                extractedData.set(extractedData.indexOf(line), prettyAddress);
                                if (fieldsList.contains(getString(R.string.ADDRESS))) {
//                                    fieldsList.remove(getString(R.string.ADDRESS));
                                    if (!addressAlreadyExists(container, prettyAddress)) {
                                        addFieldButtonAction(getString(R.string.ADDRESS), prettyAddress);
                                    }
                                    Log.println(Log.DEBUG, "MyInfo", "Address added");
                                }
                                else {
                                    Log.d( "MyInfo", "Alternate address: " + prettyAddress);
                                }
                            }

                            else {
                                // It is most likely the organization name
//                                Log.println(Log.DEBUG, "MyInfo", "The control is in organization");
                                String prettyOrgName = line.trim();
                                extractedData.set(extractedData.indexOf(line), prettyOrgName);
                                if (fieldsList.contains(getString(R.string.ORGANIZATION))) {
                                    fieldsList.remove(getString(R.string.ORGANIZATION));
                                    addFieldButtonAction(getString(R.string.ORGANIZATION), prettyOrgName);
                                    Log.println(Log.DEBUG, "MyInfo", "Organization added");
                                }
                                else {
                                    Log.d( "MyInfo", "Alternate Organization name: " + prettyOrgName);
                                }
                            }
                        }

                    } // Loop end

                    // Check if no information is fetched
//                    Log.println(Log.DEBUG, "MyInfo", "Total children: " + container.getChildCount());
                    if (container.getChildCount() == 0) {
                        Toast.makeText(MainActivity3.this, "Please scan the card again", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity3.this, "Unable to process the image", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean addressAlreadyExists(LinearLayout container, String next_address) {
        for (int i = container.getChildCount() - 1; i >= 0; i--) {
            View dynamicView = container.getChildAt(i);
            TextView label = dynamicView.findViewById(R.id.label);

            if (label.getText().equals(getString(R.string.ADDRESS))) {
                EditText textField = dynamicView.findViewById(R.id.textField);
                textField.setText(String.format("%s %s", textField.getText(), next_address));
                return true;
            }
        }
        return false;
    }

    private boolean hasMoreDigits(String line) {
        int digitCount = 0, nonDigitCount = 0;
        for (char c : line.toCharArray()) {
            if (Character.isDigit(c))
                digitCount++;
            else
                nonDigitCount++;
        }

        return digitCount > nonDigitCount;
    }

    private void openMenu() {
        // Slide in the menu from the right
        menuContainer.setVisibility(View.VISIBLE);
        menuContainer.animate().translationX(0).setDuration(300);
        footerLayout.animate().translationX(-footerLayout.getWidth()).setDuration(300);
        scrollView.animate().translationX(-scrollView.getWidth()).setDuration(300);
        isMenuOpen = true;
    }

    private void closeMenu() {
        // Slide out the menu to the right
        menuContainer.animate().translationX(menuContainer.getWidth()).setDuration(300);
        footerLayout.animate().translationX(0).setDuration(300).withEndAction(new Runnable() {
            @Override
            public void run() {
                menuContainer.setVisibility(View.GONE);
            }
        });
        scrollView.animate().translationX(0).setDuration(300);
        isMenuOpen = false;
    }

    public void displayOptions(TextView label, EditText fieldEditText, String field) {
        if (!extractedData.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity3.this);
            builder.setTitle("Select a new input data");

            // Convert the list of options to an array
            String[] optionsArray = extractedData.toArray(new String[0]);

            builder.setItems(optionsArray, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String selectedOption = optionsArray[which];
                    if (label.getText().equals(getString(R.string.ADDRESS))) {
                        int cursorPosition = fieldEditText.getSelectionStart();

                        Editable editable = fieldEditText.getText();
                        editable.insert(cursorPosition, selectedOption);
//                        fieldEditText.setText(fieldEditText.getText() + " " + selectedOption);
                    }
                    else
                        fieldEditText.setText(selectedOption);
                    dialog.dismiss();
                }
            });
            builder.setIcon(R.drawable.editblue);
            builder.show();

        } else {
            Toast.makeText(MainActivity3.this, "No alternative options available", Toast.LENGTH_SHORT).show();
        }
    }

    public void addFieldButtonAction(String label, String editText_text) {
        View dynamicView = getLayoutInflater().inflate(R.layout.activity_field, null);
        LinearLayout container = findViewById(R.id.linear_layout);

        TextView labelTextView = dynamicView.findViewById(R.id.label);
        EditText editText = dynamicView.findViewById(R.id.textField);
        Button deleteButton = dynamicView.findViewById(R.id.delete_button);
        labelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity3.this);
                builder.setTitle("Select the new field name");

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        labelTextView.setText(options[which]);
                    }
                });
                builder.setCancelable(true);
                builder.setIcon(R.drawable.editblue);
                builder.show();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView labelTextView = (TextView) dynamicView.findViewById(R.id.label);
                String fieldName = labelTextView.getText().toString();

                new AlertDialog.Builder(MainActivity3.this)
                        .setTitle("Confirm Delete")
                        .setMessage("Are you sure you want to delete this field \'" + fieldName + "\'?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                LinearLayout container = findViewById(R.id.linear_layout);
                                container.removeView(dynamicView);
                                Toast.makeText(MainActivity3.this, "Field deleted successfully", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", null)
                        .setCancelable(true)
                        .setIcon(R.drawable.remove)
                        .show();
            }
        });

        Button editButton = dynamicView.findViewById(R.id.edit_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayOptions(labelTextView, editText, (String) labelTextView.getText());
            }
        });

        labelTextView.setText(label);
        editText.setText(editText_text);
        container.addView(dynamicView);

    }

    private void deleteDuplicateOrg(LinearLayout container, String valueToCheck) {
        for (int i = container.getChildCount() - 1; i >= 0; i--) {
            View dynamicView = container.getChildAt(i);
            TextView label = dynamicView.findViewById(R.id.label);

            if (label.getText().equals(getString(R.string.ORGANIZATION))) {
                EditText textField = dynamicView.findViewById(R.id.textField);
                if (textField.getText().toString().equals(valueToCheck)) {
                    Log.d("MyInfo", "Organization name is removed because same representative name is found");
                    container.removeView(dynamicView);
                    fieldsList.add(getString(R.string.ORGANIZATION));
                }
            }
        }
    }

    private boolean isName(String str) {
        return str.matches("^[A-Za-z-'. ]+$");
    }
}

