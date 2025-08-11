import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class PomodoroSettings {
    public int workMin;
    public int breakMin;
    public int longBreakMin;
    public boolean autoStart;
    public int sessions;

    public PomodoroSettings() {
    }

    public PomodoroSettings(int workMin, int breakMin, int longBreakMin, boolean autoStart, int sessions) {
        this.workMin = workMin;
        this.breakMin = breakMin;
        this.longBreakMin = longBreakMin;
        this.autoStart = autoStart;
        this.sessions = sessions;
    }

    public int getWorkMin() {
        return workMin;
    }

    public void setWorkMin(int workMin) {
        this.workMin = workMin;
        save(); // Save on change
    }

    public int getBreakMin() {
        return breakMin;
    }

    public void setBreakMin(int breakMin) {
        this.breakMin = breakMin;
        save(); // Save on change
    }

    public int getLongBreakMin() {
        return longBreakMin;
    }

    public void setLongBreakMin(int longBreakMin) {
        this.longBreakMin = longBreakMin;
        save(); // Save on change
    }

    public boolean isAutoStart() {
        return autoStart;
    }

    public void setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
        save(); // Save on change
    }

    public int getSessions() {
        return sessions;
    }

    public void setSessions(int sessions) {
        this.sessions = sessions;
        save(); // Save on change
    }

    // ✅ Save current settings to settings.json
    public void save() {
        try (FileWriter writer = new FileWriter("settings.json")) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(this, writer);
            System.out.println("✅ Settings saved.");
        } catch (IOException e) {
            System.out.println("⚠️ Failed to save settings: " + e.getMessage());
        }
    }


    public static PomodoroSettings load() {
        try (FileReader reader = new FileReader("settings.json")) {
            Gson gson = new Gson();
            return gson.fromJson(reader, PomodoroSettings.class);
        } catch (Exception e) {
            System.out.println("⚠️ Failed to load settings. Using defaults.");
            return new PomodoroSettings(); // return default if error
        }
    }

}
