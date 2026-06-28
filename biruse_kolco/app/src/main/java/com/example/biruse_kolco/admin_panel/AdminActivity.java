package com.example.biruse_kolco.admin_panel;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.biruse_kolco.R; // ИСПРАВЛЕНО: com.example.trjwm.R → com.example.biruse_kolco.R
import com.example.biruse_kolco.data.database.AppDatabase;
import com.example.biruse_kolco.data.database.entities.Costume;
import com.example.biruse_kolco.data.database.entities.District;
import com.example.biruse_kolco.data.database.entities.GameStat;
import com.example.biruse_kolco.data.database.entities.Question;
import com.example.biruse_kolco.data.database.entities.User;
import com.example.biruse_kolco.utils.Constants;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdminActivity extends AppCompatActivity {

    private static final int SECTION_DISTRICTS = 0;
    private static final int SECTION_COSTUMES = 1;
    private static final int SECTION_QUESTIONS = 2;
    private static final int SECTION_JSON = 3;
    private static final int SECTION_STATS = 4;

    private AppDatabase database;
    private ExecutorService executor;
    private Gson gson;
    private LinearLayout menuLayout;
    private LinearLayout contentLayout;
    private int selectedSection = SECTION_DISTRICTS;

    private List<District> districts = new ArrayList<>();
    private List<Costume> costumes = new ArrayList<>();
    private List<Question> questions = new ArrayList<>();
    private List<GameStat> gameStats = new ArrayList<>();
    private User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = AppDatabase.getInstance(this);
        executor = Executors.newSingleThreadExecutor();
        gson = new GsonBuilder().setPrettyPrinting().create();
        setContentView(createRootView());
        loadData(this::showCurrentSection);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }

    // ... остальной код такой же, меняем только импорт R
    // все методы остаются без изменений

    private View createRootView() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(getColor(R.color.orel_cream));

        root.addView(createHeader());
        root.addView(createMenu());

        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));

        contentLayout = new LinearLayout(this);
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        contentLayout.setPadding(dp(16), dp(12), dp(16), dp(24));
        scrollView.addView(contentLayout, new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        root.addView(scrollView);
        return root;
    }

    private View createHeader() {
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(16), dp(18), dp(16), dp(14));
        header.setBackgroundColor(getColor(R.color.orel_green));

        MaterialButton closeButton = primaryButton("Назад");
        closeButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.orel_green_dark)));
        closeButton.setOnClickListener(v -> finish());
        header.addView(closeButton);

        LinearLayout titleBox = new LinearLayout(this);
        titleBox.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        titleParams.leftMargin = dp(12);
        titleBox.addView(text("Админ-панель", 22, R.color.white, Typeface.BOLD));
        titleBox.addView(text("Управление контентом приложения", 12,
                R.color.orel_gold_light, Typeface.NORMAL));
        header.addView(titleBox, titleParams);
        return header;
    }

    private View createMenu() {
        HorizontalScrollView scrollView = new HorizontalScrollView(this);
        scrollView.setHorizontalScrollBarEnabled(false);
        scrollView.setBackgroundColor(getColor(R.color.white));

        menuLayout = new LinearLayout(this);
        menuLayout.setOrientation(LinearLayout.HORIZONTAL);
        menuLayout.setPadding(dp(12), dp(10), dp(12), dp(10));
        scrollView.addView(menuLayout);

        refreshMenu();
        return scrollView;
    }

    private void refreshMenu() {
        menuLayout.removeAllViews();
        addMenuButton("Районы", SECTION_DISTRICTS);
        addMenuButton("Костюмы", SECTION_COSTUMES);
        addMenuButton("Вопросы", SECTION_QUESTIONS);
        addMenuButton("JSON", SECTION_JSON);
        addMenuButton("Статистика", SECTION_STATS);
    }

    private void addMenuButton(String title, int section) {
        MaterialButton button = new MaterialButton(this);
        button.setText(title);
        button.setAllCaps(false);
        boolean selected = selectedSection == section;
        button.setTextColor(getColor(selected ? R.color.white : R.color.orel_dark));
        button.setBackgroundTintList(ColorStateList.valueOf(
                getColor(selected ? R.color.orel_green : R.color.white)));
        button.setStrokeWidth(dp(1));
        button.setStrokeColor(ColorStateList.valueOf(getColor(R.color.orel_green)));
        button.setOnClickListener(v -> {
            selectedSection = section;
            refreshMenu();
            showCurrentSection();
        });

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.rightMargin = dp(8);
        menuLayout.addView(button, params);
    }

    private void loadData(Runnable afterLoad) {
        executor.execute(() -> {
            districts = database.districtDao().getAllDistricts();
            costumes = database.costumeDao().getAllCostumes();
            questions = database.questionDao().getAllQuestions();
            gameStats = database.gameStatDao().getAllGameStats();
            user = database.userDao().getUser();
            runOnUiThread(afterLoad);
        });
    }

    private void showCurrentSection() {
        if (selectedSection == SECTION_COSTUMES) {
            showCostumes();
        } else if (selectedSection == SECTION_QUESTIONS) {
            showQuestions();
        } else if (selectedSection == SECTION_JSON) {
            showJson();
        } else if (selectedSection == SECTION_STATS) {
            showStats();
        } else {
            showDistricts();
        }
    }

    // ============================================================
    // ВСЕ ОСТАЛЬНЫЕ МЕТОДЫ (showDistricts, showCostumes и т.д.)
    // ОСТАЮТСЯ БЕЗ ИЗМЕНЕНИЙ — КОПИРУЙТЕ ИХ ИЗ ВАШЕГО ФАЙЛА
    // ============================================================

    // ... (все методы от showDistricts до конца)

    private void showDistricts() {
        clearContent("Районы");

        LinearLayout summary = panel();
        addStatRow(summary, "Всего районов", String.valueOf(districts.size()));
        addStatRow(summary, "Можно редактировать", "название, описание, факты, изображения");
        addWithTopMargin(contentLayout, summary, 8);

        LinearLayout form = panel();
        Spinner spinner = spinner(districtOptions(true));
        EditText order = input("Порядок", "", false);
        order.setInputType(InputType.TYPE_CLASS_NUMBER);
        EditText name = input("Название района", "", false);
        EditText accent = input("Название с ударением", "", false);
        EditText shortHistory = input("Краткая история", "", true);
        EditText description = input("Описание", "", true);
        EditText coat = input("Герб или изображение герба", "", false);
        EditText image = input("Основное изображение", "", false);
        EditText facts = input("Интересные факты", "", true);
        EditText timeline = input("Историческая лента", "", true);
        EditText costumeDescription = input("Описание костюма района", "", true);
        SwitchMaterial completed = new SwitchMaterial(this);
        completed.setText("Район пройден пользователем");

        addWithTopMargin(form, label("Выберите район или создайте новый"), 0);
        addWithTopMargin(form, spinner, 6);
        addWithTopMargin(form, order, 10);
        addWithTopMargin(form, name, 10);
        addWithTopMargin(form, accent, 10);
        addWithTopMargin(form, shortHistory, 10);
        addWithTopMargin(form, description, 10);
        addWithTopMargin(form, coat, 10);
        addWithTopMargin(form, image, 10);
        addWithTopMargin(form, facts, 10);
        addWithTopMargin(form, timeline, 10);
        addWithTopMargin(form, costumeDescription, 10);
        addWithTopMargin(form, completed, 10);

        final District[] selected = new District[1];
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected[0] = position > 0 ? districts.get(position - 1) : null;
                fillDistrictForm(selected[0], order, name, accent, shortHistory, description,
                        coat, image, facts, timeline, costumeDescription, completed);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selected[0] = null;
            }
        });

        MaterialButton save = primaryButton("Сохранить район");
        save.setOnClickListener(v -> saveDistrict(selected[0], order, name, accent, shortHistory,
                description, coat, image, facts, timeline, costumeDescription, completed));
        addWithTopMargin(form, save, 14);
        addWithTopMargin(contentLayout, form, 12);
    }

    private void fillDistrictForm(District district, EditText order, EditText name,
                                  EditText accent, EditText shortHistory, EditText description,
                                  EditText coat, EditText image, EditText facts,
                                  EditText timeline, EditText costumeDescription,
                                  SwitchMaterial completed) {
        order.setText(String.valueOf(district == null ? districts.size() + 1 : district.getOrderIndex()));
        name.setText(district == null ? "" : safe(district.getName()));
        accent.setText(district == null ? "" : safe(district.getNameWithAccent()));
        shortHistory.setText(district == null ? "" : safe(district.getShortHistory()));
        description.setText(district == null ? "" : safe(district.getDescription()));
        coat.setText(district == null ? "" : safe(district.getCoatOfArms()));
        image.setText(district == null ? "" : safe(district.getImageUrl()));
        facts.setText(district == null ? "" : safe(district.getInterestingFacts()));
        timeline.setText(district == null ? "" : safe(district.getHistoryTimeline()));
        costumeDescription.setText(district == null ? "" : safe(district.getCostumeDescription()));
        completed.setChecked(district != null && district.isCompleted());
    }

    private void saveDistrict(District selected, EditText order, EditText name,
                              EditText accent, EditText shortHistory, EditText description,
                              EditText coat, EditText image, EditText facts,
                              EditText timeline, EditText costumeDescription,
                              SwitchMaterial completed) {
        if (read(name).isEmpty()) {
            toast("Введите название района");
            return;
        }
        executor.execute(() -> {
            District district = selected == null ? new District() : selected;
            district.setOrderIndex(parseInt(order, districts.size() + 1));
            district.setName(read(name));
            district.setNameWithAccent(read(accent));
            district.setShortHistory(read(shortHistory));
            district.setDescription(read(description));
            district.setCoatOfArms(read(coat));
            district.setImageUrl(read(image));
            district.setInterestingFacts(read(facts));
            district.setHistoryTimeline(read(timeline));
            district.setCostumeDescription(read(costumeDescription));
            district.setCompleted(completed.isChecked());
            if (selected == null) {
                database.districtDao().insertDistrict(district);
            } else {
                database.districtDao().updateDistrict(district);
            }
            syncUserDistrictTotals();
            runOnUiThread(() -> {
                toast("Район сохранен");
                loadData(this::showDistricts);
            });
        });
    }

    private void showCostumes() {
        clearContent("Костюмы");

        LinearLayout form = panel();
        Spinner costumeSpinner = spinner(costumeOptions(true));
        Spinner districtSpinner = spinner(districtOptions(false));
        EditText name = input("Название элемента костюма", "", false);
        EditText description = input("Описание", "", true);
        EditText image = input("Изображение", "", false);
        EditText model = input("3D-модель", "", false);
        SwitchMaterial unlocked = new SwitchMaterial(this);
        unlocked.setText("Доступен пользователю");

        addWithTopMargin(form, label("Выберите костюм или создайте новый"), 0);
        addWithTopMargin(form, costumeSpinner, 6);
        addWithTopMargin(form, label("Район"), 10);
        addWithTopMargin(form, districtSpinner, 6);
        addWithTopMargin(form, name, 10);
        addWithTopMargin(form, description, 10);
        addWithTopMargin(form, image, 10);
        addWithTopMargin(form, model, 10);
        addWithTopMargin(form, unlocked, 10);

        final Costume[] selected = new Costume[1];
        costumeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected[0] = position > 0 ? costumes.get(position - 1) : null;
                fillCostumeForm(selected[0], districtSpinner, name, description, image, model, unlocked);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selected[0] = null;
            }
        });

        MaterialButton save = primaryButton("Сохранить костюм");
        save.setOnClickListener(v -> saveCostume(selected[0], districtSpinner, name,
                description, image, model, unlocked));
        addWithTopMargin(form, save, 14);
        addWithTopMargin(contentLayout, form, 8);
    }

    private void fillCostumeForm(Costume costume, Spinner districtSpinner, EditText name,
                                 EditText description, EditText image, EditText model,
                                 SwitchMaterial unlocked) {
        if (costume == null) {
            if (!districts.isEmpty()) {
                districtSpinner.setSelection(0);
            }
            name.setText("");
            description.setText("");
            image.setText("");
            model.setText("");
            unlocked.setChecked(false);
            return;
        }
        selectDistrictById(districtSpinner, costume.getDistrictId());
        name.setText(safe(costume.getName()));
        description.setText(safe(costume.getDescription()));
        image.setText(safe(costume.getImageUrl()));
        model.setText(safe(costume.getModel3dUrl()));
        unlocked.setChecked(costume.isUnlocked());
    }

    private void saveCostume(Costume selected, Spinner districtSpinner, EditText name,
                             EditText description, EditText image, EditText model,
                             SwitchMaterial unlocked) {
        int districtId = selectedDistrictId(districtSpinner);
        if (districtId == 0 || read(name).isEmpty()) {
            toast("Выберите район и введите название костюма");
            return;
        }
        executor.execute(() -> {
            Costume costume = selected == null ? new Costume() : selected;
            costume.setDistrictId(districtId);
            costume.setName(read(name));
            costume.setDescription(read(description));
            costume.setImageUrl(read(image));
            costume.setModel3dUrl(read(model));
            costume.setUnlocked(unlocked.isChecked());
            if (selected == null) {
                database.costumeDao().insertCostume(costume);
            } else {
                database.costumeDao().updateCostume(costume);
            }
            runOnUiThread(() -> {
                toast("Костюм сохранен");
                loadData(this::showCostumes);
            });
        });
    }

    private void showQuestions() {
        clearContent("Вопросы");

        LinearLayout form = panel();
        Spinner questionSpinner = spinner(questionOptions(true));
        Spinner districtSpinner = spinner(districtOptions(false));
        EditText questionText = input("Текст вопроса", "", true);
        EditText optionA = input("Вариант A", "", false);
        EditText optionB = input("Вариант B", "", false);
        EditText optionC = input("Вариант C", "", false);
        Spinner correct = spinner(correctAnswerOptions());
        EditText image = input("Изображение", "", false);

        addWithTopMargin(form, label("Выберите вопрос или создайте новый"), 0);
        addWithTopMargin(form, questionSpinner, 6);
        addWithTopMargin(form, label("Район"), 10);
        addWithTopMargin(form, districtSpinner, 6);
        addWithTopMargin(form, questionText, 10);
        addWithTopMargin(form, optionA, 10);
        addWithTopMargin(form, optionB, 10);
        addWithTopMargin(form, optionC, 10);
        addWithTopMargin(form, label("Правильный ответ"), 10);
        addWithTopMargin(form, correct, 6);
        addWithTopMargin(form, image, 10);

        final Question[] selected = new Question[1];
        questionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected[0] = position > 0 ? questions.get(position - 1) : null;
                fillQuestionForm(selected[0], districtSpinner, questionText, optionA,
                        optionB, optionC, correct, image);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selected[0] = null;
            }
        });

        MaterialButton save = primaryButton("Сохранить вопрос");
        save.setOnClickListener(v -> saveQuestion(selected[0], districtSpinner, questionText,
                optionA, optionB, optionC, correct, image));
        addWithTopMargin(form, save, 14);
        addWithTopMargin(contentLayout, form, 8);
    }

    private void fillQuestionForm(Question question, Spinner districtSpinner, EditText questionText,
                                  EditText optionA, EditText optionB, EditText optionC,
                                  Spinner correct, EditText image) {
        if (question == null) {
            if (!districts.isEmpty()) {
                districtSpinner.setSelection(0);
            }
            questionText.setText("");
            optionA.setText("");
            optionB.setText("");
            optionC.setText("");
            correct.setSelection(0);
            image.setText("");
            return;
        }
        selectDistrictById(districtSpinner, question.getDistrictId());
        questionText.setText(safe(question.getQuestionText()));
        optionA.setText(safe(question.getOptionA()));
        optionB.setText(safe(question.getOptionB()));
        optionC.setText(safe(question.getOptionC()));
        correct.setSelection(Math.max(0, Math.min(2, question.getCorrectAnswer() - 1)));
        image.setText(safe(question.getImageUrl()));
    }

    private void saveQuestion(Question selected, Spinner districtSpinner, EditText questionText,
                              EditText optionA, EditText optionB, EditText optionC,
                              Spinner correct, EditText image) {
        int districtId = selectedDistrictId(districtSpinner);
        if (districtId == 0 || read(questionText).isEmpty()) {
            toast("Выберите район и введите текст вопроса");
            return;
        }
        executor.execute(() -> {
            Question question = selected == null ? new Question() : selected;
            question.setDistrictId(districtId);
            question.setQuestionText(read(questionText));
            question.setOptionA(read(optionA));
            question.setOptionB(read(optionB));
            question.setOptionC(read(optionC));
            question.setCorrectAnswer(correct.getSelectedItemPosition() + 1);
            question.setImageUrl(read(image));
            if (selected == null) {
                database.questionDao().insertQuestion(question);
            } else {
                database.questionDao().updateQuestion(question);
            }
            runOnUiThread(() -> {
                toast("Вопрос сохранен");
                loadData(this::showQuestions);
            });
        });
    }

    private void showJson() {
        clearContent("Экспорт и импорт JSON");

        LinearLayout panel = panel();
        panel.addView(text("Экспортируются и импортируются районы, костюмы и вопросы.", 14,
                R.color.orel_dark, Typeface.NORMAL));

        EditText jsonInput = input("JSON-данные", "", true);
        jsonInput.setMinLines(12);
        jsonInput.setGravity(Gravity.TOP | Gravity.START);
        addWithTopMargin(panel, jsonInput, 10);

        MaterialButton export = primaryButton("Экспортировать");
        MaterialButton copy = secondaryButton("Скопировать");
        MaterialButton importButton = dangerButton("Импортировать");
        export.setOnClickListener(v -> exportJson(jsonInput));
        copy.setOnClickListener(v -> copyToClipboard(jsonInput.getText().toString()));
        importButton.setOnClickListener(v -> confirmImport(jsonInput.getText().toString()));
        addWithTopMargin(panel, export, 12);
        addWithTopMargin(panel, copy, 8);
        addWithTopMargin(panel, importButton, 8);
        addWithTopMargin(contentLayout, panel, 8);
    }

    private void exportJson(EditText jsonInput) {
        executor.execute(() -> {
            AdminPayload payload = new AdminPayload();
            payload.version = 1;
            payload.districts = database.districtDao().getAllDistricts();
            payload.costumes = database.costumeDao().getAllCostumes();
            payload.questions = database.questionDao().getAllQuestions();
            String json = gson.toJson(payload);
            runOnUiThread(() -> {
                jsonInput.setText(json);
                copyToClipboard(json);
                toast("JSON экспортирован и скопирован");
            });
        });
    }

    private void confirmImport(String json) {
        if (json.trim().isEmpty()) {
            toast("Вставьте JSON для импорта");
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Импорт JSON")
                .setMessage("Текущие районы, костюмы и вопросы будут заменены данными из JSON.")
                .setPositiveButton("Импортировать", (dialog, which) -> importJson(json))
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void importJson(String json) {
        executor.execute(() -> {
            try {
                AdminPayload payload = gson.fromJson(json, AdminPayload.class);
                if (payload == null) {
                    throw new IllegalArgumentException("JSON пустой");
                }
                database.runInTransaction(() -> {
                    database.questionDao().deleteAllQuestions();
                    database.costumeDao().deleteAllCostumes();
                    database.districtDao().deleteAllDistricts();
                    if (payload.districts != null) {
                        database.districtDao().insertAllDistricts(payload.districts);
                    }
                    if (payload.costumes != null) {
                        database.costumeDao().insertAllCostumes(payload.costumes);
                    }
                    if (payload.questions != null) {
                        database.questionDao().insertAllQuestions(payload.questions);
                    }
                    syncUserDistrictTotals();
                });
                runOnUiThread(() -> {
                    toast("JSON импортирован");
                    loadData(this::showJson);
                });
            } catch (Exception exception) {
                runOnUiThread(() -> toast("Ошибка импорта: " + exception.getMessage()));
            }
        });
    }

    private void showStats() {
        clearContent("Статистика");

        LinearLayout userPanel = panel();
        if (user == null) {
            addStatRow(userPanel, "Пользователь", "не найден");
        } else {
            addStatRow(userPanel, "Имя", safe(user.getUserName()));
            addStatRow(userPanel, "Уровень", String.valueOf(user.getLevel()));
            addStatRow(userPanel, "Очки", String.valueOf(user.getPoints()));
            addStatRow(userPanel, "Районы", user.getCompletedDistricts() + " / " + user.getTotalDistricts());
            addStatRow(userPanel, "Общий счет", String.valueOf(user.getTotalScore()));
            addStatRow(userPanel, "Игр сыграно", String.valueOf(user.getGamesPlayed()));
        }
        addWithTopMargin(contentLayout, userPanel, 8);

        LinearLayout contentPanel = panel();
        addStatRow(contentPanel, "Районы", String.valueOf(districts.size()));
        addStatRow(contentPanel, "Костюмы", String.valueOf(costumes.size()));
        addStatRow(contentPanel, "Вопросы", String.valueOf(questions.size()));
        addWithTopMargin(contentLayout, contentPanel, 12);

        LinearLayout gamesPanel = panel();
        gamesPanel.addView(text("Статистика игр", 17, R.color.orel_dark, Typeface.BOLD));
        if (gameStats.isEmpty()) {
            addWithTopMargin(gamesPanel, text("Пока нет записей по играм", 14,
                    R.color.orel_gray, Typeface.NORMAL), 8);
        } else {
            for (GameStat stat : gameStats) {
                addStatRow(gamesPanel, safe(stat.getGameType()),
                        "очки: " + stat.getScore()
                                + ", рекорд: " + stat.getHighScore()
                                + ", игр: " + stat.getGamesPlayed()
                                + ", побед: " + stat.getGamesWon());
            }
        }
        MaterialButton refresh = primaryButton("Обновить статистику");
        refresh.setOnClickListener(v -> loadData(this::showStats));
        addWithTopMargin(gamesPanel, refresh, 12);
        addWithTopMargin(contentLayout, gamesPanel, 12);
    }

    private void clearContent(String title) {
        contentLayout.removeAllViews();
        contentLayout.addView(text(title, 24, R.color.orel_dark, Typeface.BOLD));
    }

    private LinearLayout panel() {
        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(dp(14), dp(14), dp(14), dp(14));
        GradientDrawable background = new GradientDrawable();
        background.setColor(getColor(R.color.white));
        background.setCornerRadius(dp(12));
        panel.setBackground(background);
        panel.setElevation(dp(2));
        return panel;
    }

    private TextView text(String value, int sizeSp, int colorRes, int style) {
        TextView textView = new TextView(this);
        textView.setText(value);
        textView.setTextSize(sizeSp);
        textView.setTextColor(getColor(colorRes));
        textView.setTypeface(Typeface.DEFAULT, style);
        return textView;
    }

    private TextView label(String value) {
        return text(value, 13, R.color.orel_gray, Typeface.BOLD);
    }

    private EditText input(String hint, String value, boolean multiline) {
        EditText editText = new EditText(this);
        editText.setHint(hint);
        editText.setText(value);
        editText.setTextColor(getColor(R.color.orel_dark));
        editText.setHintTextColor(getColor(R.color.orel_gray));
        editText.setTextSize(14);
        editText.setSingleLine(!multiline);
        if (multiline) {
            editText.setMinLines(3);
            editText.setGravity(Gravity.TOP | Gravity.START);
            editText.setInputType(InputType.TYPE_CLASS_TEXT
                    | InputType.TYPE_TEXT_FLAG_MULTI_LINE
                    | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        }
        return editText;
    }

    private Spinner spinner(List<String> items) {
        Spinner spinner = new Spinner(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        return spinner;
    }

    private MaterialButton primaryButton(String title) {
        MaterialButton button = new MaterialButton(this);
        button.setText(title);
        button.setAllCaps(false);
        button.setTextColor(getColor(R.color.white));
        button.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.orel_green)));
        return button;
    }

    private MaterialButton secondaryButton(String title) {
        MaterialButton button = new MaterialButton(this);
        button.setText(title);
        button.setAllCaps(false);
        button.setTextColor(getColor(R.color.orel_dark));
        button.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.orel_gold_light)));
        return button;
    }

    private MaterialButton dangerButton(String title) {
        MaterialButton button = new MaterialButton(this);
        button.setText(title);
        button.setAllCaps(false);
        button.setTextColor(getColor(R.color.white));
        button.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.orel_red)));
        return button;
    }

    private void addWithTopMargin(LinearLayout parent, View view, int topMarginDp) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = dp(topMarginDp);
        parent.addView(view, params);
    }

    private void addStatRow(LinearLayout parent, String name, String value) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        TextView nameView = text(name, 14, R.color.orel_gray, Typeface.BOLD);
        TextView valueView = text(value, 14, R.color.orel_dark, Typeface.NORMAL);
        valueView.setGravity(Gravity.END);
        row.addView(nameView, new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 0.9f));
        row.addView(valueView, new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1.1f));
        addWithTopMargin(parent, row, 8);
    }

    private List<String> districtOptions(boolean includeNew) {
        List<String> options = new ArrayList<>();
        if (includeNew) {
            options.add("Новый район");
        }
        for (District district : districts) {
            options.add("#" + district.getId() + " " + safe(district.getName()));
        }
        return options;
    }

    private List<String> costumeOptions(boolean includeNew) {
        List<String> options = new ArrayList<>();
        if (includeNew) {
            options.add("Новый костюм");
        }
        for (Costume costume : costumes) {
            options.add("#" + costume.getId() + " " + safe(costume.getName()));
        }
        return options;
    }

    private List<String> questionOptions(boolean includeNew) {
        List<String> options = new ArrayList<>();
        if (includeNew) {
            options.add("Новый вопрос");
        }
        for (Question question : questions) {
            String text = safe(question.getQuestionText());
            if (text.length() > 36) {
                text = text.substring(0, 36) + "...";
            }
            options.add("#" + question.getId() + " " + text);
        }
        return options;
    }

    private List<String> correctAnswerOptions() {
        List<String> options = new ArrayList<>();
        options.add("1 - вариант A");
        options.add("2 - вариант B");
        options.add("3 - вариант C");
        return options;
    }

    private void selectDistrictById(Spinner spinner, int districtId) {
        for (int i = 0; i < districts.size(); i++) {
            if (districts.get(i).getId() == districtId) {
                spinner.setSelection(i);
                return;
            }
        }
        if (!districts.isEmpty()) {
            spinner.setSelection(0);
        }
    }

    private int selectedDistrictId(Spinner spinner) {
        int position = spinner.getSelectedItemPosition();
        if (position < 0 || position >= districts.size()) {
            return 0;
        }
        return districts.get(position).getId();
    }

    private String read(EditText input) {
        return input.getText().toString().trim();
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private int parseInt(EditText input, int fallback) {
        try {
            return Integer.parseInt(read(input));
        } catch (NumberFormatException exception) {
            return fallback;
        }
    }

    private void copyToClipboard(String value) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            clipboard.setPrimaryClip(ClipData.newPlainText(Constants.ADMIN_JSON_CLIPBOARD_LABEL, value));
            toast("Скопировано");
        }
    }

    private void syncUserDistrictTotals() {
        User currentUser = database.userDao().getUser();
        if (currentUser != null) {
            currentUser.setTotalDistricts(database.districtDao().getTotalCount());
            currentUser.setCompletedDistricts(database.districtDao().getCompletedCount());
            database.userDao().updateUser(currentUser);
        }
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private static class AdminPayload {
        int version;
        List<District> districts;
        List<Costume> costumes;
        List<Question> questions;
    }
}