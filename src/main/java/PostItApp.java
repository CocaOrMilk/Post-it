import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import com.google.gson.*;

class Note {
    String text;
    int x, y;
    int width, height;
    int colorIndex;
    boolean pinned;
    int customR, customG, customB;
    boolean isCustomColor;
    int fontSize;
    boolean isBold;
    int textColorR, textColorG, textColorB;
    boolean isCustomTextColor;
    String htmlContent;

    public Note(String text, int x, int y, int width, int height, int colorIndex, boolean pinned) {
        this.text = text;
        this.htmlContent = null;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.colorIndex = colorIndex;
        this.pinned = pinned;
        this.isCustomColor = false;
        this.customR = 255;
        this.customG = 255;
        this.customB = 153;
        this.fontSize = 16;
        this.isBold = false;
        this.textColorR = 0;
        this.textColorG = 0;
        this.textColorB = 0;
        this.isCustomTextColor = false;
    }
}

public class PostItApp {

    static java.util.List<Note> notes = new ArrayList<>();
    static java.util.List<JFrame> frames = new ArrayList<>();
    static java.util.List<JTextPane> textPanes = new ArrayList<>();

    static final String FILE_NAME = "notes.json";

    static Color[] colors = {
            new Color(255, 255, 153),
            new Color(153, 255, 153),
            new Color(153, 204, 255),
            new Color(255, 204, 153)
    };

    // 7 CORES DO ARCO-IRIS (versoes claras/pastel)
    static Color[] rainbowColors = {
            new Color(255, 179, 186),  // Vermelho claro
            new Color(255, 223, 186),  // Laranja claro
            new Color(255, 255, 186),  // Amarelo claro
            new Color(186, 255, 201),  // Verde claro
            new Color(186, 225, 255),  // Azul claro
            new Color(186, 186, 255),  // Anil claro
            new Color(225, 186, 255)   // Violeta claro
    };

    static Random random = new Random();

    public static void main(String[] args) {
        loadNotes();

        if (notes.isEmpty()) {
            int randomColor = random.nextInt(rainbowColors.length);
            Note n = new Note("", 200, 200, 334, 347, randomColor, false);
            n.isCustomColor = true;
            n.customR = rainbowColors[randomColor].getRed();
            n.customG = rainbowColors[randomColor].getGreen();
            n.customB = rainbowColors[randomColor].getBlue();
            notes.add(n);
            createNote(n);
        } else {
            for (Note note : notes) {
                createNote(note);
            }
        }
    }

    static void createNote(Note note) {

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTextPane textPane = new JTextPane();
        textPane.setContentType("text/html");

        // Converter Enter em <br> ao inves de <p> para melhor preservacao de quebras
        textPane.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    e.consume();
                    try {
                        HTMLDocument doc = (HTMLDocument) textPane.getDocument();
                        HTMLEditorKit kit = (HTMLEditorKit) textPane.getEditorKit();
                        kit.insertHTML(doc, textPane.getCaretPosition(), "<br>", 0, 0, HTML.Tag.BR);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        if (note.htmlContent != null && !note.htmlContent.isEmpty()) {
            String html = note.htmlContent;
            if (!html.contains("font-family")) {
                html = html.replace("<body>", "<body style='font-family: Arial; font-size: " + note.fontSize + "pt;'>");
            }
            // Garantir que paragrafos tenham margem 0 mas preservem quebras de linha
            if (!html.contains("p {")) {
                html = html.replace("</head>", "<style>p { margin: 0; display: block; }</style></head>");
            }
            textPane.setText(html);
        } else {
            String initialText = note.text != null ? note.text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;") : "";
            String html = "<html><body style='font-family: Arial; font-size: " + note.fontSize + "pt;'>" +
                          initialText.replace("\n", "<br>") + "</body></html>";
            textPane.setText(html);
        }

        if (note.isCustomColor) {
            textPane.setBackground(new Color(note.customR, note.customG, note.customB));
        } else {
            textPane.setBackground(colors[note.colorIndex]);
        }

        frames.add(frame);
        textPanes.add(textPane);

        JScrollPane scroll = new JScrollPane(textPane);

        JButton newBtn = new JButton("+");
        JButton saveBtn = new JButton("💾");
        JButton pinBtn = new JButton("📌");
        JButton deleteBtn = new JButton("🗑️");
        JButton closeBtn = new JButton("X");

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbar.add(newBtn);
        toolbar.add(saveBtn);
        toolbar.add(pinBtn);
        toolbar.add(deleteBtn);
        toolbar.add(closeBtn);

        JMenuBar menuBar = new JMenuBar();
        JMenu editMenu = new JMenu("Editar");

        JMenuItem colorItem = new JMenuItem("🎨 Cor de fundo");
        JMenuItem textColorItem = new JMenuItem("A Cor do texto");
        JMenuItem boldItem = new JMenuItem(note.isBold ? "Negrito" : "Negrito");
        JMenuItem fontUpItem = new JMenuItem("+ Aumentar fonte");
        JMenuItem fontDownItem = new JMenuItem("- Diminuir fonte");

        editMenu.add(colorItem);
        editMenu.add(textColorItem);
        editMenu.addSeparator();
        editMenu.add(boldItem);
        editMenu.add(fontUpItem);
        editMenu.add(fontDownItem);
        menuBar.add(editMenu);

        JPanel top = new JPanel(new BorderLayout());
        top.add(menuBar, BorderLayout.WEST);
        top.add(toolbar, BorderLayout.EAST);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(top, BorderLayout.NORTH);
        contentPanel.add(scroll, BorderLayout.CENTER);

        JPanel borderNorth = new JPanel();
        JPanel borderSouth = new JPanel();
        JPanel borderWest = new JPanel();
        JPanel borderEast = new JPanel();
        borderNorth.setOpaque(false);
        borderSouth.setOpaque(false);
        borderWest.setOpaque(false);
        borderEast.setOpaque(false);
        borderNorth.setPreferredSize(new Dimension(0, 6));
        borderSouth.setPreferredSize(new Dimension(0, 6));
        borderWest.setPreferredSize(new Dimension(6, 0));
        borderEast.setPreferredSize(new Dimension(6, 0));

        JPanel cornerNW = new JPanel();
        JPanel cornerNE = new JPanel();
        JPanel cornerSW = new JPanel();
        JPanel cornerSE = new JPanel();
        cornerNW.setOpaque(false);
        cornerNE.setOpaque(false);
        cornerSW.setOpaque(false);
        cornerSE.setOpaque(false);
        cornerNW.setPreferredSize(new Dimension(6, 6));
        cornerNE.setPreferredSize(new Dimension(6, 6));
        cornerSW.setPreferredSize(new Dimension(6, 6));
        cornerSE.setPreferredSize(new Dimension(6, 6));

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(cornerNW, BorderLayout.WEST);
        northPanel.add(borderNorth, BorderLayout.CENTER);
        northPanel.add(cornerNE, BorderLayout.EAST);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(cornerSW, BorderLayout.WEST);
        southPanel.add(borderSouth, BorderLayout.CENTER);
        southPanel.add(cornerSE, BorderLayout.EAST);

        JPanel resizeWrapper = new JPanel(new BorderLayout());
        resizeWrapper.add(northPanel, BorderLayout.NORTH);
        resizeWrapper.add(southPanel, BorderLayout.SOUTH);
        resizeWrapper.add(borderWest, BorderLayout.WEST);
        resizeWrapper.add(borderEast, BorderLayout.EAST);
        resizeWrapper.add(contentPanel, BorderLayout.CENTER);

        frame.setLayout(new BorderLayout());
        frame.add(resizeWrapper, BorderLayout.CENTER);

        frame.setBounds(note.x, note.y, note.width, note.height);
        frame.setUndecorated(true);
        frame.setMinimumSize(new Dimension(150, 150));

        if (note.pinned) {
            frame.setAlwaysOnTop(true);
            textPane.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        }

        // ➕ NOVA NOTA (com cor aleatoria do arco-iris)
        newBtn.addActionListener(e -> {
            Point loc = frame.getLocation();
            int randomColor = random.nextInt(rainbowColors.length);
            Note newNote = new Note("", loc.x + 50, loc.y + 50, 334, 347, 0, false);
            newNote.isCustomColor = true;
            newNote.customR = rainbowColors[randomColor].getRed();
            newNote.customG = rainbowColors[randomColor].getGreen();
            newNote.customB = rainbowColors[randomColor].getBlue();
            notes.add(newNote);
            createNote(newNote);
        });

        // 💾 SALVAR (MANUAL)
        saveBtn.addActionListener(e -> {
            updateAllNotes();
            saveAll();
            JOptionPane.showMessageDialog(frame, "Notas salvas!");
        });

        // 🎨 COR DE FUNDO
        colorItem.addActionListener(e -> {
            Color currentColor;
            if (note.isCustomColor) {
                currentColor = new Color(note.customR, note.customG, note.customB);
            } else {
                currentColor = colors[note.colorIndex];
            }

            Color chosen = JColorChooser.showDialog(frame, "Escolher cor de fundo", currentColor);
            if (chosen != null) {
                textPane.setBackground(chosen);
                note.isCustomColor = true;
                note.customR = chosen.getRed();
                note.customG = chosen.getGreen();
                note.customB = chosen.getBlue();
            }
        });

        // A COR DO TEXTO
        textColorItem.addActionListener(e -> {
            Color currentTextColor;
            if (note.isCustomTextColor) {
                currentTextColor = new Color(note.textColorR, note.textColorG, note.textColorB);
            } else {
                currentTextColor = Color.BLACK;
            }

            Color chosen = JColorChooser.showDialog(frame, "Escolher cor do texto", currentTextColor);

            if (chosen != null) {
                applyColorToSelection(textPane, chosen);
            }
        });

        // 📌 PIN
        pinBtn.addActionListener(e -> {
            boolean state = !frame.isAlwaysOnTop();
            frame.setAlwaysOnTop(state);
            textPane.setBorder(state ? BorderFactory.createLineBorder(Color.RED, 2) : null);
            note.pinned = state;
        });

        // 🗑️ DELETAR NOTA
        deleteBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(frame,
                "Tem certeza que deseja deletar esta nota?",
                "Confirmar exclusao",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                int index = frames.indexOf(frame);
                if (index != -1) {
                    frames.remove(index);
                    textPanes.remove(index);
                    notes.remove(index);
                }
                frame.dispose();
                saveAll();
            }
        });

        // B NEGRITO (aplica na selecao)
        boldItem.addActionListener(e -> {
            toggleBoldOnSelection(textPane);
        });

        // A+ AUMENTAR FONTE (aplica na selecao)
        fontUpItem.addActionListener(e -> {
            changeFontSizeOnSelection(textPane, 2);
        });

        // A- DIMINUIR FONTE (aplica na selecao)
        fontDownItem.addActionListener(e -> {
            changeFontSizeOnSelection(textPane, -2);
        });

        // ❌ FECHAR
        closeBtn.addActionListener(e -> {
            int index = frames.indexOf(frame);

            if (index != -1) {
                frames.remove(index);
                textPanes.remove(index);
                notes.remove(index);
            }

            frame.dispose();
        });

        // 🔥 ARRASTAR (so na barra superior) + RESIZE (nas bordas)
        int[] resizeDir = {0};
        Point[] startMouse = {null};
        Rectangle[] startBounds = {null};

        applyDrag(top, frame, startMouse, startBounds);
        applyResizeFixed(borderNorth, frame, resizeDir, startMouse, startBounds, 1);
        applyResizeFixed(borderSouth, frame, resizeDir, startMouse, startBounds, 2);
        applyResizeFixed(borderWest, frame, resizeDir, startMouse, startBounds, 3);
        applyResizeFixed(borderEast, frame, resizeDir, startMouse, startBounds, 4);
        applyResizeFixed(cornerNW, frame, resizeDir, startMouse, startBounds, 5);
        applyResizeFixed(cornerNE, frame, resizeDir, startMouse, startBounds, 6);
        applyResizeFixed(cornerSW, frame, resizeDir, startMouse, startBounds, 7);
        applyResizeFixed(cornerSE, frame, resizeDir, startMouse, startBounds, 8);

        frame.setVisible(true);
    }

    // 🔥 ATUALIZA TODAS AS NOTAS
    static void updateAllNotes() {
        for (int i = 0; i < notes.size(); i++) {
            Note note = notes.get(i);
            JFrame frame = frames.get(i);
            JTextPane textPane = textPanes.get(i);

            note.text = textPane.getText();
            note.x = frame.getX();
            note.y = frame.getY();
            note.width = frame.getWidth();
            note.height = frame.getHeight();
            note.pinned = frame.isAlwaysOnTop();

            Color bg = textPane.getBackground();
            boolean matched = false;
            for (int j = 0; j < colors.length; j++) {
                if (colors[j].equals(bg)) {
                    note.colorIndex = j;
                    note.isCustomColor = false;
                    matched = true;
                    break;
                }
            }
            if (!matched) {
                note.isCustomColor = true;
                note.customR = bg.getRed();
                note.customG = bg.getGreen();
                note.customB = bg.getBlue();
            }

            Color fg = textPane.getForeground();
            if (fg.equals(Color.BLACK)) {
                note.isCustomTextColor = false;
            } else {
                note.isCustomTextColor = true;
                note.textColorR = fg.getRed();
                note.textColorG = fg.getGreen();
                note.textColorB = fg.getBlue();
            }

            note.htmlContent = textPane.getText();
            note.text = textPane.getText();
        }
    }

    // 🔥 APLICA COR NA SELECAO
    static void applyColorToSelection(JTextPane textPane, Color color) {
        int start = textPane.getSelectionStart();
        int end = textPane.getSelectionEnd();
        if (start == end) return;

        SimpleAttributeSet attrs = new SimpleAttributeSet();
        StyleConstants.setForeground(attrs, color);
        textPane.getStyledDocument().setCharacterAttributes(start, end - start, attrs, false);
    }

    // 🔥 TOGGLE NEGRITO NA SELECAO
    static void toggleBoldOnSelection(JTextPane textPane) {
        int start = textPane.getSelectionStart();
        int end = textPane.getSelectionEnd();
        if (start == end) return;

        StyledDocument doc = textPane.getStyledDocument();
        AttributeSet currentAttrs = doc.getCharacterElement(start).getAttributes();
        boolean isBold = StyleConstants.isBold(currentAttrs);

        SimpleAttributeSet attrs = new SimpleAttributeSet();
        StyleConstants.setBold(attrs, !isBold);
        doc.setCharacterAttributes(start, end - start, attrs, false);
    }

    // 🔥 MUDA TAMANHO DA FONTE NA SELECAO
    static void changeFontSizeOnSelection(JTextPane textPane, int delta) {
        int start = textPane.getSelectionStart();
        int end = textPane.getSelectionEnd();
        if (start == end) return;

        StyledDocument doc = textPane.getStyledDocument();
        AttributeSet currentAttrs = doc.getCharacterElement(start).getAttributes();
        int currentSize = StyleConstants.getFontSize(currentAttrs);
        int newSize = Math.max(8, Math.min(48, currentSize + delta));

        SimpleAttributeSet attrs = new SimpleAttributeSet();
        StyleConstants.setFontSize(attrs, newSize);
        doc.setCharacterAttributes(start, end - start, attrs, false);
    }

    static void saveAll() {
        try {
            Gson gson = new Gson();
            FileWriter writer = new FileWriter(FILE_NAME);
            gson.toJson(notes, writer);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void loadNotes() {
        try {
            Gson gson = new Gson();
            Reader reader = new FileReader(FILE_NAME);

            Note[] loaded = gson.fromJson(reader, Note[].class);

            if (loaded != null) {
                notes = new ArrayList<>(Arrays.asList(loaded));
            }

            reader.close();
        } catch (Exception e) {
            notes = new ArrayList<>();
        }
    }

    // 🔥 ARRASTAR (so na barra superior)
    static void applyDrag(Component comp, JFrame frame,
                          Point[] startMouse,
                          Rectangle[] startBounds) {

        comp.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                startMouse[0] = e.getLocationOnScreen();
                startBounds[0] = frame.getBounds();
            }
        });

        comp.addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                Point current = e.getLocationOnScreen();
                int dx = current.x - startMouse[0].x;
                int dy = current.y - startMouse[0].y;
                frame.setLocation(
                    startBounds[0].x + dx,
                    startBounds[0].y + dy
                );
            }
        });
    }

    // 🔥 RESIZE FIXO (direcao: 1=N, 2=S, 3=W, 4=E, 5=NW, 6=NE, 7=SW, 8=SE)
    static void applyResizeFixed(Component comp, JFrame frame,
                                 int[] resizeDir,
                                 Point[] startMouse,
                                 Rectangle[] startBounds,
                                 int fixedDir) {

        int[] cursorTypes = {0, Cursor.N_RESIZE_CURSOR, Cursor.S_RESIZE_CURSOR,
                             Cursor.W_RESIZE_CURSOR, Cursor.E_RESIZE_CURSOR,
                             Cursor.NW_RESIZE_CURSOR, Cursor.NE_RESIZE_CURSOR,
                             Cursor.SW_RESIZE_CURSOR, Cursor.SE_RESIZE_CURSOR};

        comp.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                startMouse[0] = e.getLocationOnScreen();
                startBounds[0] = frame.getBounds();
                resizeDir[0] = fixedDir;
            }
        });

        comp.addMouseMotionListener(new MouseAdapter() {
            public void mouseMoved(MouseEvent e) {
                frame.setCursor(Cursor.getPredefinedCursor(cursorTypes[fixedDir]));
            }

            public void mouseDragged(MouseEvent e) {
                Point current = e.getLocationOnScreen();
                int dx = current.x - startMouse[0].x;
                int dy = current.y - startMouse[0].y;

                Rectangle r = new Rectangle(startBounds[0]);
                int minW = 150;
                int minH = 150;

                switch (resizeDir[0]) {
                    case 1: r.y += dy; r.height -= dy; break;
                    case 2: r.height += dy; break;
                    case 3: r.x += dx; r.width -= dx; break;
                    case 4: r.width += dx; break;
                    case 5: r.x += dx; r.y += dy; r.width -= dx; r.height -= dy; break;
                    case 6: r.y += dy; r.width += dx; r.height -= dy; break;
                    case 7: r.x += dx; r.width -= dx; r.height += dy; break;
                    case 8: r.width += dx; r.height += dy; break;
                }

                if (r.width >= minW && r.height >= minH) {
                    frame.setBounds(r);
                }
            }
        });
    }
}