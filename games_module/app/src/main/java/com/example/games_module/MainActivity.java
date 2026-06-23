package com.example.games_module;

import android.content.ClipData;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.InputFilter;
import android.text.InputType;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private static final int TURQUOISE = Color.rgb(23, 162, 184);
    private static final int GOLD = Color.rgb(255, 215, 0);
    private static final int NUT = Color.rgb(139, 115, 85);
    private static final int RED = Color.rgb(204, 0, 0);
    private static final int BLUE = Color.rgb(30, 58, 138);
    private static final int BEIGE = Color.rgb(245, 240, 232);
    private static final int DARK = Color.rgb(42, 36, 32);
    private static final int PAPER = Color.WHITE;
    private static final int SUCCESS = Color.rgb(70, 140, 88);
    private static final int ERROR = Color.rgb(180, 62, 62);

    private static final String[] GAME_KEYS = {
            "quiz",
            "crossword",
            "coloring",
            "guess",
            "costume",
            "puzzle",
            "memory",
            "difference",
            "truth"
    };

    private static final CostumeItem[] COSTUME_ITEMS = {
            new CostumeItem("Кокошник", "head"),
            new CostumeItem("Сарафан", "body"),
            new CostumeItem("Пояс", "waist"),
            new CostumeItem("Лапти", "feet"),
            new CostumeItem("Герб района", "wrong"),
            new CostumeItem("Карта", "wrong")
    };

    private FrameLayout root;
    private SharedPreferences preferences;
    private final Random random = new Random();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean hubVisible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        preferences = getSharedPreferences("developer_4_games_progress", MODE_PRIVATE);
        root = new FrameLayout(this);
        root.setId(R.id.main);
        root.setBackgroundColor(BEIGE);
        setContentView(root);

        ViewCompat.setOnApplyWindowInsetsListener(root, (view, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        showHub();
    }

    @Override
    public void onBackPressed() {
        if (!hubVisible) {
            showHub();
            return;
        }
        super.onBackPressed();
    }

    private void showHub() {
        LinearLayout content = createScreen(
                "Игры и развлечения",
                "Все работают автономно и сохраняют лучший результат.",
                false,
                true);

        TextView progress = bodyText(
                "Пройдено мини-игр: " + getCompletedCount() + " из " + GAME_KEYS.length,
                16,
                DARK,
                Typeface.BOLD);
        progress.setGravity(Gravity.CENTER);
        progress.setBackground(rounded(PAPER, TURQUOISE, 2, 8));
        progress.setPadding(dp(14), dp(12), dp(14), dp(12));
        content.addView(progress, fullWidthWithBottomMargin(16));

        content.addView(gameTile(
                "1. Викторина",
                "10 вопросов по районам из маршрута.",
                "quiz",
                view -> showChoiceGame(
                        "quiz",
                        "Викторина",
                        "10 вопросов по районам Орловской области.",
                        GameData.QUIZ_QUESTIONS)));
        content.addView(gameTile(
                "2. Кроссворд",
                "Генерируемая сетка 5x5 с тематическими словами.",
                "crossword",
                view -> showCrosswordGame()));
        content.addView(gameTile(
                "3. Раскраски",
                "Шаблон костюма, 12 цветов, кисть, заливка и ластик.",
                "coloring",
                view -> showColoringGame()));
        content.addView(gameTile(
                "4. Угадай район",
                "Подсказка и выбор из 3 вариантов.",
                "guess",
                view -> showChoiceGame(
                        "guess",
                        "Угадай район",
                        "Читай подсказку и выбирай один из трех районов.",
                        GameData.GUESS_DISTRICT_QUESTIONS)));
        content.addView(gameTile(
                "5. Собери костюм",
                "Перетащи элементы на правильные части костюма.",
                "costume",
                view -> showCostumeGame()));
        content.addView(gameTile(
                "6. Пазл",
                "Собери стилизованную картинку народного наряда.",
                "puzzle",
                view -> showPuzzleGame()));
        content.addView(gameTile(
                "7. Мемори",
                "Открывай карточки и находи пары элементов костюма.",
                "memory",
                view -> showMemoryGame()));
        content.addView(gameTile(
                "8. Найди отличие",
                "Сравни два варианта костюма и найди 4 отличия.",
                "difference",
                view -> showDifferenceGame()));
        content.addView(gameTile(
                "9. Правда/Неправда",
                "Факты по ТЗ, районам и игровым механикам.",
                "truth",
                view -> showChoiceGame(
                        "truth",
                        "Правда/Неправда",
                        "Решай, верно ли утверждение.",
                        GameData.TRUE_FALSE_QUESTIONS)));
    }

    private void showChoiceGame(String key, String title, String subtitle, GameData.ChoiceQuestion[] questions) {
        LinearLayout content = createScreen(title, subtitle, true, false);

        TextView progress = bodyText("", 15, DARK, Typeface.BOLD);
        TextView prompt = bodyText("", 20, DARK, Typeface.BOLD);
        prompt.setBackground(rounded(PAPER, TURQUOISE, 2, 8));
        prompt.setPadding(dp(16), dp(16), dp(16), dp(16));

        LinearLayout optionsBox = verticalLayout(0);
        TextView feedback = bodyText("", 15, DARK, Typeface.NORMAL);
        feedback.setMinHeight(dp(54));

        Button next = primaryButton("Дальше");
        next.setVisibility(View.GONE);

        content.addView(progress, fullWidthWithBottomMargin(10));
        content.addView(prompt, fullWidthWithBottomMargin(14));
        content.addView(optionsBox, fullWidthWithBottomMargin(8));
        content.addView(feedback, fullWidthWithBottomMargin(8));
        content.addView(next, fullWidthWithBottomMargin(12));

        int[] index = { 0 };
        int[] score = { 0 };
        Runnable[] render = new Runnable[1];

        render[0] = () -> {
            GameData.ChoiceQuestion question = questions[index[0]];
            progress.setText("Вопрос " + (index[0] + 1) + " из " + questions.length + " · Баллы: " + score[0]);
            prompt.setText(question.prompt);
            feedback.setText("");
            next.setVisibility(View.GONE);
            optionsBox.removeAllViews();

            for (int i = 0; i < question.options.length; i++) {
                int optionIndex = i;
                Button option = outlineButton((i + 1) + ". " + question.options[i]);
                option.setGravity(Gravity.CENTER_VERTICAL);
                option.setOnClickListener(view -> {
                    boolean correct = optionIndex == question.correctIndex;
                    if (correct) {
                        score[0]++;
                    }

                    for (int childIndex = 0; childIndex < optionsBox.getChildCount(); childIndex++) {
                        Button child = (Button) optionsBox.getChildAt(childIndex);
                        child.setEnabled(false);
                        if (childIndex == question.correctIndex) {
                            child.setBackground(rounded(SUCCESS, SUCCESS, 1, 8));
                            child.setTextColor(PAPER);
                        }
                    }
                    if (!correct) {
                        option.setBackground(rounded(ERROR, ERROR, 1, 8));
                        option.setTextColor(PAPER);
                    }
                    feedback.setText((correct ? "Верно. " : "Почти. ") + question.hint);
                    next.setText(index[0] == questions.length - 1 ? "Завершить" : "Дальше");
                    next.setVisibility(View.VISIBLE);
                });
                optionsBox.addView(option, fullWidthWithBottomMargin(10));
            }
        };

        next.setOnClickListener(view -> {
            index[0]++;
            if (index[0] >= questions.length) {
                showResultScreen(
                        key,
                        title,
                        score[0],
                        questions.length,
                        "Игра завершена. Лучший результат сохранен.",
                        () -> showChoiceGame(key, title, subtitle, questions));
            } else {
                render[0].run();
            }
        });

        render[0].run();
    }

    private void showCrosswordGame() {
        LinearLayout content = createScreen(
                "Кроссворд 5x5",
                "Заполни строки по подсказкам. Черные клетки не используются.",
                true,
                false);

        TextView status = bodyText("Сгенерируй сетку и заполни слова.", 15, DARK, Typeface.BOLD);
        status.setBackground(rounded(PAPER, TURQUOISE, 2, 8));
        status.setPadding(dp(14), dp(12), dp(14), dp(12));
        content.addView(status, fullWidthWithBottomMargin(12));

        LinearLayout gameBox = verticalLayout(0);
        content.addView(gameBox, fullWidthWithBottomMargin(12));

        Button regenerate = outlineButton("Сгенерировать новую сетку");
        content.addView(regenerate, fullWidthWithBottomMargin(10));

        Runnable[] render = new Runnable[1];
        render[0] = () -> renderCrossword(gameBox, status);
        regenerate.setOnClickListener(view -> render[0].run());
        render[0].run();
    }

    private void renderCrossword(LinearLayout gameBox, TextView status) {
        gameBox.removeAllViews();
        CrosswordSession session = newCrosswordSession();

        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(5);
        grid.setRowCount(5);
        grid.setUseDefaultMargins(false);
        grid.setAlignmentMode(GridLayout.ALIGN_BOUNDS);
        // grid.setGravity(Gravity.CENTER);

        for (int row = 0; row < 5; row++) {
            for (int column = 0; column < 5; column++) {
                if (!session.active[row][column]) {
                    TextView blank = new TextView(this);
                    blank.setBackground(rounded(NUT, NUT, 1, 6));
                    grid.addView(blank, cellParams());
                    continue;
                }

                EditText cell = new EditText(this);
                cell.setGravity(Gravity.CENTER);
                cell.setTextColor(DARK);
                cell.setTextSize(18);
                cell.setTypeface(Typeface.DEFAULT_BOLD);
                cell.setSingleLine(true);
                cell.setFilters(new InputFilter[] { new InputFilter.LengthFilter(1) });
                cell.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                cell.setSelectAllOnFocus(true);
                cell.setBackground(rounded(PAPER, TURQUOISE, 2, 6));
                session.cells[row][column] = cell;
                grid.addView(cell, cellParams());
            }
        }

        gameBox.addView(grid, centeredWithBottomMargin(16));

        TextView cluesTitle = bodyText("Подсказки", 17, DARK, Typeface.BOLD);
        gameBox.addView(cluesTitle, fullWidthWithBottomMargin(6));
        for (int i = 0; i < session.words.size(); i++) {
            GameData.WordClue word = session.words.get(i);
            TextView clue = bodyText((i + 1) + ". " + word.clue + " (" + word.word.length() + " букв)", 15, DARK,
                    Typeface.NORMAL);
            gameBox.addView(clue, fullWidthWithBottomMargin(4));
        }

        LinearLayout controls = horizontalLayout(8);
        Button hint = outlineButton("Подсказка Маруси");
        Button check = primaryButton("Проверить");
        controls.addView(hint, weightedButtonParams());
        controls.addView(check, weightedButtonParams());
        gameBox.addView(controls, fullWidthWithBottomMargin(8));

        hint.setOnClickListener(view -> revealCrosswordLetter(session, status));
        check.setOnClickListener(view -> checkCrossword(session, status));
    }

    private CrosswordSession newCrosswordSession() {
        List<GameData.WordClue> words = new ArrayList<>(Arrays.asList(GameData.CROSSWORD_WORDS));
        Collections.shuffle(words, random);
        words = new ArrayList<>(words.subList(0, 5));

        CrosswordSession session = new CrosswordSession();
        session.words = words;
        for (int row = 0; row < 5; row++) {
            String word = words.get(row).word;
            for (int column = 0; column < 5; column++) {
                if (column < word.length()) {
                    session.active[row][column] = true;
                    session.answers[row][column] = word.charAt(column);
                    session.totalCells++;
                }
            }
        }
        return session;
    }

    private void revealCrosswordLetter(CrosswordSession session, TextView status) {
        for (int row = 0; row < 5; row++) {
            for (int column = 0; column < 5; column++) {
                EditText cell = session.cells[row][column];
                if (session.active[row][column] && cell != null && cell.getText().toString().trim().isEmpty()) {
                    cell.setText(String.valueOf(session.answers[row][column]));
                    cell.setBackground(rounded(GOLD, GOLD, 1, 6));
                    status.setText("Маруся открыла одну букву. Остальное за тобой.");
                    return;
                }
            }
        }
        status.setText("Все клетки уже заполнены. Можно проверять.");
    }

    private void checkCrossword(CrosswordSession session, TextView status) {
        hideKeyboard();
        int correct = 0;
        for (int row = 0; row < 5; row++) {
            for (int column = 0; column < 5; column++) {
                if (!session.active[row][column]) {
                    continue;
                }
                EditText cell = session.cells[row][column];
                String value = cell.getText().toString().trim().toUpperCase(new Locale("ru"));
                boolean right = value.equals(String.valueOf(session.answers[row][column]));
                if (right) {
                    correct++;
                    cell.setBackground(rounded(SUCCESS, SUCCESS, 1, 6));
                } else {
                    cell.setBackground(rounded(ERROR, ERROR, 1, 6));
                }
                cell.setTextColor(PAPER);
            }
        }

        if (correct == session.totalCells) {
            markGameCompleted("crossword", 5, 5);
            status.setText("Кроссворд решен полностью. Награда сохранена.");
        } else {
            status.setText("Верных клеток: " + correct + " из " + session.totalCells + ". Исправь красные буквы.");
        }
    }

    private void showColoringGame() {
        LinearLayout content = createScreen(
                "Раскраска",
                "Раскрась народный костюм: заливка, кисть, ластик и 12 цветов.",
                true,
                false);

        TextView status = bodyText("Выбери инструмент и цвет, затем коснись шаблона.", 15, DARK, Typeface.BOLD);
        status.setBackground(rounded(PAPER, TURQUOISE, 2, 8));
        status.setPadding(dp(14), dp(12), dp(14), dp(12));
        content.addView(status, fullWidthWithBottomMargin(12));

        ColoringView coloringView = new ColoringView(this);
        coloringView.setBackgroundColor(BEIGE);
        content.addView(coloringView, fixedHeightWithBottomMargin(430, 12));

        LinearLayout tools = horizontalLayout(8);
        Button fill = primaryButton("Заливка");
        Button brush = outlineButton("Кисть");
        Button eraser = outlineButton("Ластик");
        tools.addView(fill, weightedButtonParams());
        tools.addView(brush, weightedButtonParams());
        tools.addView(eraser, weightedButtonParams());
        content.addView(tools, fullWidthWithBottomMargin(12));

        int[] palette = {
                TURQUOISE, GOLD, NUT, RED, BLUE, BEIGE,
                DARK, Color.rgb(76, 140, 78), Color.rgb(255, 137, 67),
                Color.rgb(167, 83, 167), Color.rgb(255, 255, 255), Color.rgb(120, 190, 210)
        };
        GridLayout paletteGrid = new GridLayout(this);
        paletteGrid.setColumnCount(6);
        for (int color : palette) {
            TextView swatch = new TextView(this);
            swatch.setBackground(rounded(color, DARK, 1, 8));
            swatch.setOnClickListener(view -> {
                coloringView.setSelectedColor(color);
                status.setText("Цвет выбран. Можно раскрашивать.");
            });
            paletteGrid.addView(swatch, swatchParams());
        }
        content.addView(paletteGrid, centeredWithBottomMargin(14));

        fill.setOnClickListener(view -> {
            coloringView.setTool(ColoringView.TOOL_FILL);
            status.setText("Инструмент: заливка.");
        });
        brush.setOnClickListener(view -> {
            coloringView.setTool(ColoringView.TOOL_BRUSH);
            status.setText("Инструмент: кисть.");
        });
        eraser.setOnClickListener(view -> {
            coloringView.setTool(ColoringView.TOOL_ERASER);
            status.setText("Инструмент: ластик.");
        });

        LinearLayout actions = horizontalLayout(8);
        Button reset = outlineButton("Сбросить");
        Button save = primaryButton("Сохранить");
        actions.addView(reset, weightedButtonParams());
        actions.addView(save, weightedButtonParams());
        content.addView(actions, fullWidthWithBottomMargin(12));

        reset.setOnClickListener(view -> {
            coloringView.resetTemplate();
            status.setText("Шаблон очищен.");
        });
        save.setOnClickListener(view -> saveColoring(coloringView, status));
    }

    private void saveColoring(ColoringView coloringView, TextView status) {
        File baseDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (baseDirectory == null) {
            baseDirectory = getFilesDir();
        }
        File directory = new File(baseDirectory, "coloring_gallery");
        if (!directory.exists() && !directory.mkdirs()) {
            Toast.makeText(this, "Не удалось создать папку для сохранения", Toast.LENGTH_SHORT).show();
            return;
        }

        File file = new File(directory, "costume_coloring_" + System.currentTimeMillis() + ".png");
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            Bitmap bitmap = coloringView.exportBitmap();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            MediaScannerConnection.scanFile(this, new String[] { file.getAbsolutePath() }, new String[] { "image/png" },
                    null);
            markGameCompleted("coloring", Math.max(1, coloringView.getColoredZoneCount()),
                    Math.max(1, coloringView.getZoneCount()));
            status.setText("Раскраска сохранена: " + file.getName());
            Toast.makeText(this, "Раскраска сохранена", Toast.LENGTH_SHORT).show();
        } catch (IOException exception) {
            Toast.makeText(this, "Ошибка сохранения раскраски", Toast.LENGTH_SHORT).show();
        }
    }

    private void showCostumeGame() {
        LinearLayout content = createScreen(
                "Собери костюм",
                "Перетащи или выбери элементы и помести их в правильные зоны.",
                true,
                false);

        TextView status = bodyText("Правильно собрано: 0 из 4.", 15, DARK, Typeface.BOLD);
        status.setBackground(rounded(PAPER, TURQUOISE, 2, 8));
        status.setPadding(dp(14), dp(12), dp(14), dp(12));
        content.addView(status, fullWidthWithBottomMargin(12));

        TextView selectedText = bodyText("Выбран элемент: нет", 14, DARK, Typeface.NORMAL);
        content.addView(selectedText, fullWidthWithBottomMargin(10));

        Map<String, CostumeItem> placed = new HashMap<>();
        CostumeItem[] selected = { null };

        LinearLayout slots = verticalLayout(8);
        String[][] slotData = {
                { "head", "Голова" },
                { "body", "Корпус" },
                { "waist", "Талия" },
                { "feet", "Обувь" }
        };
        for (String[] item : slotData) {
            TextView slot = costumeSlot(item[0], item[1]);
            slot.setOnClickListener(view -> {
                if (selected[0] != null) {
                    placeCostumeItem((TextView) view, selected[0], placed, status);
                }
            });
            slot.setOnDragListener((view, event) -> {
                if (event.getAction() == DragEvent.ACTION_DROP && event.getLocalState() instanceof CostumeItem) {
                    placeCostumeItem((TextView) view, (CostumeItem) event.getLocalState(), placed, status);
                    return true;
                }
                return true;
            });
            slots.addView(slot, fullWidthWithBottomMargin(8));
        }
        content.addView(slots, fullWidthWithBottomMargin(12));

        TextView listTitle = bodyText("Элементы", 17, DARK, Typeface.BOLD);
        content.addView(listTitle, fullWidthWithBottomMargin(8));

        GridLayout itemGrid = new GridLayout(this);
        itemGrid.setColumnCount(2);
        for (CostumeItem item : COSTUME_ITEMS) {
            Button itemButton = outlineButton(item.title);
            itemButton.setOnClickListener(view -> {
                selected[0] = item;
                selectedText.setText("Выбран элемент: " + item.title);
            });
            itemButton.setOnLongClickListener(view -> {
                ClipData clipData = ClipData.newPlainText("costume_item", item.title);
                view.startDragAndDrop(clipData, new View.DragShadowBuilder(view), item, 0);
                return true;
            });
            itemGrid.addView(itemButton, gridButtonParams(2));
        }
        content.addView(itemGrid, centeredWithBottomMargin(14));

        Button reset = outlineButton("Сбросить костюм");
        reset.setOnClickListener(view -> showCostumeGame());
        content.addView(reset, fullWidthWithBottomMargin(8));
    }

    private void placeCostumeItem(TextView slotView, CostumeItem item, Map<String, CostumeItem> placed,
            TextView status) {
        String slot = (String) slotView.getTag();
        placed.put(slot, item);
        boolean correct = slot.equals(item.slot);
        slotView.setText(slotView.getContentDescription() + "\n" + item.title);
        slotView.setTextColor(correct ? PAPER : DARK);
        slotView.setBackground(correct ? rounded(SUCCESS, SUCCESS, 1, 8) : rounded(GOLD, GOLD, 1, 8));

        int correctCount = countCorrectCostumeItems(placed);
        status.setText("Правильно собрано: " + correctCount + " из 4.");
        if (correctCount == 4) {
            markGameCompleted("costume", 4, 4);
            status.setText("Костюм собран правильно. Бонус сохранен.");
        }
    }

    private int countCorrectCostumeItems(Map<String, CostumeItem> placed) {
        int count = 0;
        for (Map.Entry<String, CostumeItem> entry : placed.entrySet()) {
            if (entry.getKey().equals(entry.getValue().slot)) {
                count++;
            }
        }
        return count;
    }

    private TextView costumeSlot(String slot, String label) {
        TextView view = bodyText(label + "\nперетащи сюда", 16, DARK, Typeface.BOLD);
        view.setTag(slot);
        view.setContentDescription(label);
        view.setGravity(Gravity.CENTER);
        view.setMinHeight(dp(72));
        view.setBackground(rounded(PAPER, TURQUOISE, 2, 8));
        view.setPadding(dp(12), dp(10), dp(12), dp(10));
        return view;
    }

    private void showPuzzleGame() {
        LinearLayout content = createScreen(
                "Пазл",
                "Собери картинку наряда. Нажми две плитки, чтобы поменять их местами.",
                true,
                false);

        TextView status = bodyText("Плитки перемешаны.", 15, DARK, Typeface.BOLD);
        status.setBackground(rounded(PAPER, TURQUOISE, 2, 8));
        status.setPadding(dp(14), dp(12), dp(14), dp(12));
        content.addView(status, fullWidthWithBottomMargin(12));

        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(3);
        content.addView(grid, centeredWithBottomMargin(12));

        int[] tiles = shuffledPuzzle();
        int[] selectedIndex = { -1 };
        Runnable[] render = new Runnable[1];
        render[0] = () -> {
            grid.removeAllViews();
            for (int position = 0; position < tiles.length; position++) {
                int currentPosition = position;
                int tile = tiles[position];
                Button button = puzzleTileButton(tile, selectedIndex[0] == position);
                button.setOnClickListener(view -> {
                    if (selectedIndex[0] == -1) {
                        selectedIndex[0] = currentPosition;
                        status.setText("Выбрана плитка " + (tile + 1) + ". Теперь выбери, с чем поменять.");
                    } else if (selectedIndex[0] == currentPosition) {
                        selectedIndex[0] = -1;
                        status.setText("Выбор снят.");
                    } else {
                        int temp = tiles[selectedIndex[0]];
                        tiles[selectedIndex[0]] = tiles[currentPosition];
                        tiles[currentPosition] = temp;
                        selectedIndex[0] = -1;
                        if (isPuzzleSolved(tiles)) {
                            markGameCompleted("puzzle", 9, 9);
                            status.setText("Пазл собран. Картинка наряда восстановлена.");
                        } else {
                            status.setText("Плитки поменялись местами.");
                        }
                    }
                    render[0].run();
                });
                grid.addView(button, puzzleParams());
            }
        };
        render[0].run();

        LinearLayout actions = horizontalLayout(8);
        Button shuffle = outlineButton("Перемешать");
        Button solve = outlineButton("Показать порядок");
        actions.addView(shuffle, weightedButtonParams());
        actions.addView(solve, weightedButtonParams());
        content.addView(actions, fullWidthWithBottomMargin(8));

        shuffle.setOnClickListener(view -> {
            int[] fresh = shuffledPuzzle();
            System.arraycopy(fresh, 0, tiles, 0, tiles.length);
            selectedIndex[0] = -1;
            status.setText("Пазл перемешан заново.");
            render[0].run();
        });
        solve.setOnClickListener(view -> status.setText("Порядок: 1-2-3, 4-5-6, 7-8-9."));
    }

    private int[] shuffledPuzzle() {
        int[] tiles = { 0, 1, 2, 3, 4, 5, 6, 7, 8 };
        do {
            for (int i = tiles.length - 1; i > 0; i--) {
                int swapIndex = random.nextInt(i + 1);
                int temp = tiles[i];
                tiles[i] = tiles[swapIndex];
                tiles[swapIndex] = temp;
            }
        } while (isPuzzleSolved(tiles));
        return tiles;
    }

    private boolean isPuzzleSolved(int[] tiles) {
        for (int i = 0; i < tiles.length; i++) {
            if (tiles[i] != i) {
                return false;
            }
        }
        return true;
    }

    private Button puzzleTileButton(int tile, boolean selected) {
        int[] colors = {
                TURQUOISE, GOLD, PAPER,
                PAPER, RED, PAPER,
                BLUE, RED, NUT
        };
        Button button = new Button(this);
        button.setAllCaps(false);
        button.setText((tile + 1) + "\n" + GameData.PUZZLE_TILES[tile]);
        button.setTextSize(12);
        button.setGravity(Gravity.CENTER);
        int color = selected ? GOLD : colors[tile];
        int stroke = selected ? DARK : TURQUOISE;
        button.setBackground(rounded(color, stroke, selected ? 3 : 1, 8));
        button.setTextColor(color == BLUE || color == RED || color == NUT ? PAPER : DARK);
        return button;
    }

    private void showMemoryGame() {
        LinearLayout content = createScreen(
                "Мемори",
                "Открой две карточки. Если элементы совпали, пара остается открытой.",
                true,
                false);

        TextView status = bodyText("Пары: 0 из " + GameData.MEMORY_PAIRS.length, 15, DARK, Typeface.BOLD);
        status.setBackground(rounded(PAPER, TURQUOISE, 2, 8));
        status.setPadding(dp(14), dp(12), dp(14), dp(12));
        content.addView(status, fullWidthWithBottomMargin(12));

        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(3);
        content.addView(grid, centeredWithBottomMargin(12));

        List<String> cards = new ArrayList<>();
        for (String pair : GameData.MEMORY_PAIRS) {
            cards.add(pair);
            cards.add(pair);
        }
        Collections.shuffle(cards, random);
        String[] deck = cards.toArray(new String[0]);
        boolean[] open = new boolean[deck.length];
        boolean[] matched = new boolean[deck.length];
        int[] firstIndex = { -1 };
        int[] moves = { 0 };
        boolean[] locked = { false };

        Runnable[] render = new Runnable[1];
        render[0] = () -> {
            grid.removeAllViews();
            int pairs = countMatchedPairs(matched);
            status.setText("Пары: " + pairs + " из " + GameData.MEMORY_PAIRS.length + " · Ходы: " + moves[0]);
            for (int index = 0; index < deck.length; index++) {
                int cardIndex = index;
                Button card = outlineButton(open[index] || matched[index] ? deck[index] : "?");
                card.setTextSize(open[index] || matched[index] ? 12 : 22);
                if (matched[index]) {
                    card.setBackground(rounded(SUCCESS, SUCCESS, 1, 8));
                    card.setTextColor(PAPER);
                }
                card.setOnClickListener(view -> {
                    if (locked[0] || matched[cardIndex] || open[cardIndex]) {
                        return;
                    }
                    open[cardIndex] = true;
                    if (firstIndex[0] == -1) {
                        firstIndex[0] = cardIndex;
                        render[0].run();
                        return;
                    }

                    moves[0]++;
                    int previous = firstIndex[0];
                    firstIndex[0] = -1;
                    if (deck[previous].equals(deck[cardIndex])) {
                        matched[previous] = true;
                        matched[cardIndex] = true;
                        if (countMatchedPairs(matched) == GameData.MEMORY_PAIRS.length) {
                            markGameCompleted("memory", GameData.MEMORY_PAIRS.length, GameData.MEMORY_PAIRS.length);
                            status.setText("Все пары найдены. Результат сохранен.");
                        }
                        render[0].run();
                    } else {
                        locked[0] = true;
                        render[0].run();
                        handler.postDelayed(() -> {
                            open[previous] = false;
                            open[cardIndex] = false;
                            locked[0] = false;
                            render[0].run();
                        }, 700);
                    }
                });
                grid.addView(card, memoryParams());
            }
        };
        render[0].run();

        Button restart = outlineButton("Начать заново");
        restart.setOnClickListener(view -> showMemoryGame());
        content.addView(restart, fullWidthWithBottomMargin(8));
    }

    private int countMatchedPairs(boolean[] matched) {
        int count = 0;
        for (boolean item : matched) {
            if (item) {
                count++;
            }
        }
        return count / 2;
    }

    private void showDifferenceGame() {
        LinearLayout content = createScreen(
                "Найди отличие",
                "Коснись отличий между двумя вариантами костюма.",
                true,
                false);

        TextView status = bodyText("Найдено: 0 из 4.", 15, DARK, Typeface.BOLD);
        status.setBackground(rounded(PAPER, TURQUOISE, 2, 8));
        status.setPadding(dp(14), dp(12), dp(14), dp(12));
        content.addView(status, fullWidthWithBottomMargin(12));

        SpotDifferenceView differenceView = new SpotDifferenceView(this);
        differenceView.setOnDifferenceFoundListener((foundCount, totalCount) -> {
            status.setText("Найдено: " + foundCount + " из " + totalCount + ".");
            if (foundCount == totalCount) {
                markGameCompleted("difference", totalCount, totalCount);
                status.setText("Все отличия найдены. Награда сохранена.");
            }
        });
        content.addView(differenceView, fixedHeightWithBottomMargin(430, 12));

        Button reset = outlineButton("Сбросить");
        reset.setOnClickListener(view -> differenceView.reset());
        content.addView(reset, fullWidthWithBottomMargin(8));
    }

    private void showResultScreen(String key, String title, int score, int maxScore, String message, Runnable retry) {
        markGameCompleted(key, score, maxScore);
        LinearLayout content = createScreen("Результат", title, true, false);

        TextView result = bodyText(score + " из " + maxScore, 30, TURQUOISE, Typeface.BOLD);
        result.setGravity(Gravity.CENTER);
        result.setBackground(rounded(PAPER, TURQUOISE, 2, 8));
        result.setPadding(dp(16), dp(20), dp(16), dp(20));
        content.addView(result, fullWidthWithBottomMargin(14));

        TextView details = bodyText(message, 16, DARK, Typeface.NORMAL);
        details.setGravity(Gravity.CENTER);
        content.addView(details, fullWidthWithBottomMargin(16));

        Button again = primaryButton("Повторить");
        again.setOnClickListener(view -> retry.run());
        content.addView(again, fullWidthWithBottomMargin(10));

        Button hub = outlineButton("К списку игр");
        hub.setOnClickListener(view -> showHub());
        content.addView(hub, fullWidthWithBottomMargin(8));
    }

    private LinearLayout createScreen(String title, String subtitle, boolean withBack, boolean isHub) {
        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        scrollView.setBackgroundColor(BEIGE);

        LinearLayout content = verticalLayout(0);
        content.setPadding(dp(20), dp(18), dp(20), dp(26));
        scrollView.addView(content, new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        if (withBack) {
            TextView back = bodyText("< К играм", 15, TURQUOISE, Typeface.BOLD);
            back.setPadding(0, 0, 0, dp(8));
            back.setOnClickListener(view -> showHub());
            content.addView(back, fullWidthWithBottomMargin(4));
        }

        TextView titleView = bodyText(title, 28, DARK, Typeface.BOLD);
        content.addView(titleView, fullWidthWithBottomMargin(6));

        TextView subtitleView = bodyText(subtitle, 15, DARK, Typeface.NORMAL);
        subtitleView.setLineSpacing(2f, 1.05f);
        content.addView(subtitleView, fullWidthWithBottomMargin(18));

        setScreen(scrollView, isHub);
        return content;
    }

    private void setScreen(View screen, boolean isHub) {
        hubVisible = isHub;
        root.removeAllViews();
        root.addView(screen, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private LinearLayout gameTile(String title, String description, String key, View.OnClickListener listener) {
        LinearLayout tile = verticalLayout(4);
        tile.setPadding(dp(16), dp(14), dp(16), dp(14));
        tile.setBackground(rounded(PAPER, TURQUOISE, 1, 8));
        tile.setClickable(true);
        tile.setFocusable(true);
        tile.setOnClickListener(listener);

        TextView titleView = bodyText(title, 18, DARK, Typeface.BOLD);
        TextView descriptionView = bodyText(description, 14, DARK, Typeface.NORMAL);
        TextView progressView = bodyText(progressTextFor(key), 13, TURQUOISE, Typeface.BOLD);

        tile.addView(titleView, fullWidthWithBottomMargin(4));
        tile.addView(descriptionView, fullWidthWithBottomMargin(6));
        tile.addView(progressView, fullWidthWithBottomMargin(0));
        tile.setLayoutParams(fullWidthWithBottomMargin(10));
        return tile;
    }

    private String progressTextFor(String key) {
        boolean done = preferences.getBoolean(key + "_done", false);
        int best = preferences.getInt(key + "_best", -1);
        int max = preferences.getInt(key + "_max", 0);
        if (!done) {
            return "Статус: не пройдено";
        }
        if (best >= 0 && max > 0) {
            return "Статус: пройдено · лучший результат " + best + "/" + max;
        }
        return "Статус: пройдено";
    }

    private int getCompletedCount() {
        int count = 0;
        for (String key : GAME_KEYS) {
            if (preferences.getBoolean(key + "_done", false)) {
                count++;
            }
        }
        return count;
    }

    private void markGameCompleted(String key, int score, int maxScore) {
        int previousBest = preferences.getInt(key + "_best", Integer.MIN_VALUE);
        SharedPreferences.Editor editor = preferences.edit()
                .putBoolean(key + "_done", true)
                .putInt(key + "_max", maxScore);
        if (score > previousBest) {
            editor.putInt(key + "_best", score);
        }
        editor.apply();
    }

    private TextView bodyText(String text, int sp, int color, int style) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize(sp);
        textView.setTextColor(color);
        textView.setTypeface(Typeface.DEFAULT, style);
        textView.setIncludeFontPadding(true);
        return textView;
    }

    private Button primaryButton(String text) {
        Button button = new Button(this);
        button.setAllCaps(false);
        button.setText(text);
        button.setTextColor(PAPER);
        button.setTextSize(15);
        button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        button.setBackground(rounded(TURQUOISE, TURQUOISE, 1, 8));
        button.setMinHeight(dp(48));
        return button;
    }

    private Button outlineButton(String text) {
        Button button = new Button(this);
        button.setAllCaps(false);
        button.setText(text);
        button.setTextColor(DARK);
        button.setTextSize(15);
        button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        button.setBackground(rounded(PAPER, TURQUOISE, 2, 8));
        button.setMinHeight(dp(48));
        return button;
    }

    private GradientDrawable rounded(int fillColor, int strokeColor, int strokeWidthDp, int radiusDp) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(fillColor);
        drawable.setCornerRadius(dp(radiusDp));
        drawable.setStroke(dp(strokeWidthDp), strokeColor);
        return drawable;
    }

    private LinearLayout verticalLayout(int dividerDp) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setDividerPadding(dp(dividerDp));
        return layout;
    }

    private LinearLayout horizontalLayout(int gapDp) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER);
        layout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        layout.setDividerDrawable(new android.graphics.drawable.ColorDrawable(Color.TRANSPARENT) {
            @Override
            public int getIntrinsicWidth() {
                return dp(gapDp);
            }
        });
        return layout;
    }

    private LinearLayout.LayoutParams fullWidthWithBottomMargin(int bottomDp) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, dp(bottomDp));
        return params;
    }

    private LinearLayout.LayoutParams fixedHeightWithBottomMargin(int heightDp, int bottomDp) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(heightDp));
        params.setMargins(0, 0, 0, dp(bottomDp));
        return params;
    }

    private LinearLayout.LayoutParams centeredWithBottomMargin(int bottomDp) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.setMargins(0, 0, 0, dp(bottomDp));
        return params;
    }

    private LinearLayout.LayoutParams weightedButtonParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        return params;
    }

    private GridLayout.LayoutParams cellParams() {
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = dp(50);
        params.height = dp(50);
        params.setMargins(dp(3), dp(3), dp(3), dp(3));
        return params;
    }

    private GridLayout.LayoutParams swatchParams() {
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = dp(42);
        params.height = dp(42);
        params.setMargins(dp(4), dp(4), dp(4), dp(4));
        return params;
    }

    private GridLayout.LayoutParams gridButtonParams(int columns) {
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = dp(columns == 2 ? 150 : 96);
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.setMargins(dp(4), dp(4), dp(4), dp(4));
        return params;
    }

    private GridLayout.LayoutParams puzzleParams() {
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = dp(94);
        params.height = dp(94);
        params.setMargins(dp(4), dp(4), dp(4), dp(4));
        return params;
    }

    private GridLayout.LayoutParams memoryParams() {
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = dp(96);
        params.height = dp(76);
        params.setMargins(dp(4), dp(4), dp(4), dp(4));
        return params;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private void hideKeyboard() {
        View currentFocus = getCurrentFocus();
        if (currentFocus == null) {
            return;
        }
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
    }

    private static final class CrosswordSession {
        final boolean[][] active = new boolean[5][5];
        final char[][] answers = new char[5][5];
        final EditText[][] cells = new EditText[5][5];
        List<GameData.WordClue> words;
        int totalCells;
    }

    private static final class CostumeItem {
        final String title;
        final String slot;

        CostumeItem(String title, String slot) {
            this.title = title;
            this.slot = slot;
        }
    }
}
