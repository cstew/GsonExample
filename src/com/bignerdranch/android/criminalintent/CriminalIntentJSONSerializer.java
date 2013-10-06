package com.bignerdranch.android.criminalintent;

import android.content.Context;
import android.os.Environment;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CriminalIntentJSONSerializer {
    private static final String FOLDER_NAME = "/CriminalIntent";

    private Context mContext;
    private String mFilename;

    public CriminalIntentJSONSerializer(Context c, String f) {
        mContext = c;
        mFilename = f;
    }

    public ArrayList<Crime> loadCrimes() throws IOException, JSONException {
        ArrayList<Crime> crimes = new ArrayList<Crime>();
        BufferedReader reader = null;

        try {
            // open and read the file into a StringBuilder
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File (sdCard.getAbsolutePath() + FOLDER_NAME);
            dir.mkdirs();
            File file = new File(dir, mFilename);

            InputStream in = new FileInputStream(file);
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                // line breaks are omitted and irrelevant
                jsonString.append(line);
            }
            crimes = getCrimes(jsonString.toString());
        } catch (FileNotFoundException e) {
            // we will ignore this one, since it happens when we start fresh
        } finally {
            if (reader != null)
                reader.close();
        }

        return crimes;
    }

    public void saveCrimes(ArrayList<Crime> crimes) throws JSONException, IOException {
        // build an array in JSON

        String json = getJson(crimes);

        // write the file to disk
        Writer writer = null;
        try {
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File (sdCard.getAbsolutePath() + FOLDER_NAME);
            dir.mkdirs();
            File file = new File(dir, mFilename);

            FileOutputStream out = new FileOutputStream(file);
            writer = new OutputStreamWriter(out);
            writer.write(json);
        } finally {
            if (writer != null)
                writer.close();
        }
    }

    private String getJson(List<Crime> crimes) {
        Gson gson = new Gson();
        return gson.toJson(crimes);
    }

    // Easy Way

    private ArrayList<Crime> getCrimes(String json) throws JSONException {
        Gson gson = new Gson();
        ArrayList<Crime> crimes = new ArrayList<Crime>();

        // parse the JSON using JSONTokener
        JSONArray array = new JSONArray(json.toString());
        // build the array of crimes from JSONObjects
        for (int i = 0; i < array.length(); i++) {
            String crimeJson = array.get(i).toString();
            Crime crime = gson.fromJson(crimeJson, Crime.class);
            crimes.add(crime);
        }
        return crimes;
    }

    // Harder Way

//    private ArrayList<Crime> getCrimes(String json) throws JSONException {
//        Gson gson = new Gson();
//        Type crimeListType = new TypeToken<List<Crime>>() {}.getType();
//        return gson.fromJson(json, crimeListType);
//    }
}
