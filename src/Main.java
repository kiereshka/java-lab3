
import java.util.*;
import java.util.stream.Collectors;
import java.io.*;
import java.util.Comparator;

interface Drawable
{
    void draw();
}

abstract class Shape implements Drawable, Serializable
{
    private String shapeColor;

    public Shape(String shapeColor)
    {
        this.shapeColor = shapeColor;
    }

    public String getShapeColor()
    {
        return shapeColor;
    }

    public abstract double calcArea();

    @Override
    public String toString()
    {
        return "Фігура{" +
                "колір фігури='" + shapeColor + '\'' +
                ", площа=" + calcArea() +
                '}';
    }

    @Override
    public void draw()
    {
        System.out.println("В процесі малювання " + this.getClass().getSimpleName() + " з кольору " + shapeColor);
    }

}

class Rectangle extends Shape
{
    private double width;
    private double height;

    public Rectangle(String shapeColor, double width, double height)
    {
        super(shapeColor);
        this.width = width;
        this.height = height;
    }

    @Override
    public double calcArea()
    {
        return width * height;
    }
}

class Triangle extends Shape
{
    private double base;
    private double height;

    public Triangle(String shapeColor, double base, double height)
    {
        super(shapeColor);
        this.base = base;
        this.height = height;
    }

    @Override
    public double calcArea()
    {
        return 0.5 * base * height;
    }
}

class Circle extends Shape
{
    private double radius;

    public Circle(String shapeColor, double radius)
    {
        super(shapeColor);
        this.radius = radius;
    }

    @Override
    public double calcArea()
    {
        return Math.PI * radius * radius;
    }
}

class Model
{
    private List<Shape> shapes;

    public Model()
    {
        shapes = new ArrayList<>();
    }

    public void addShape(Shape shape)
    {
        shapes.add(shape);
    }

    public List<Shape> getShapes()
    {
        return shapes;
    }
}

class View
{
    public void displayShapes(List<Shape> shapes)
    {
        System.out.println("Фігури");
        for (Shape shape : shapes)
        {
            shape.draw();
        }
    }

    public void displayTotalArea(double totalArea) {
        System.out.println("Загальна площа всіх фігур: " + totalArea);
    }

    public void displayTotalAreaByColor(String color, double totalArea)
    {
        System.out.println("Загальна площа фігур з кольором" + color + " Коло: " + totalArea);
    }

    public void displaySortedShapes(List<Shape> shapes)
    {
        System.out.println("Сортування фігур за збільшенням площі:");
        shapes.forEach(System.out::println);
    }

    public void displaySortedShapesByColor(List<Shape> shapes)
    {
        System.out.println("Сортування фігур за кольором:");
        shapes.forEach(System.out::println);
    }

    public void displayMenu()
    {
        System.out.println("\nДобрий день!");
        System.out.println("1. Вивести всі фігури");
        System.out.println("2. Вирахувати загальну площу фігур");
        System.out.println("3. Вирахувати загальну площу фігур за кольором");
        System.out.println("4. Сортувати фігури за їх площею");
        System.out.println("5. Сортувати фігури за їх кольором");
        System.out.println("6. Зберегти фігури у файл");
        System.out.println("7. Завантажити фігури з файлу");
        System.out.println("8. Вийти з меню");
        System.out.print("Оберіть варіант: ");
    }
}

class ShapeFileHandler {
    public static void saveShapesToFile(List<Shape> shapes, String fileName) {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(fileName))) {
            outputStream.writeObject(shapes);
            System.out.println("Дані були збережені у файл " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @SuppressWarnings("unchecked")
    public static List<Shape> loadShapesFromFile(String fileName) {
        List<Shape> shapes = null;
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(fileName))) {
            shapes = (List<Shape>) inputStream.readObject();
            System.out.println("Дані були завантажені з файлу " + fileName);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return shapes;
    }
}

class Controller
{
    private Model model;
    private View view;

    public Controller(Model model, View view)
    {
        this.model = model;
        this.view = view;
    }

    public void controlMenu()
    {
        Scanner scanner = new Scanner(System.in);
        boolean exitMenu = false;

        while (!exitMenu)
        {
            view.displayMenu();
            int userChoice = scanner.nextInt();
            scanner.nextLine();

            switch (userChoice)
            {
                case 1:
                    view.displayShapes(model.getShapes());
                    break;
                case 2:
                    double totalArea = model.getShapes().stream().mapToDouble(Shape::calcArea).sum();
                    view.displayTotalArea(totalArea);
                    break;
                case 3:
                    System.out.print("Введіть колір:");
                    String color = scanner.nextLine();
                    double totalAreaByColor = model.getShapes().stream()
                            .filter(shape -> shape.getShapeColor().equalsIgnoreCase(color))
                            .mapToDouble(Shape::calcArea)
                            .sum();
                    view.displayTotalAreaByColor(color, totalAreaByColor);
                    break;
                case 4:
                    List<Shape> sortedByArea = model.getShapes().stream()
                            .sorted(Comparator.comparingDouble(Shape::calcArea))
                            .collect(Collectors.toList());
                    view.displaySortedShapes(sortedByArea);
                    break;
                case 5:
                    List<Shape> sortedByColor = model.getShapes().stream()
                            .sorted(Comparator.comparing(shape -> shape.getShapeColor().toLowerCase()))
                            .collect(Collectors.toList());
                    view.displaySortedShapesByColor(sortedByColor);
                    break;
                case 6:
                    ShapeFileHandler.saveShapesToFile(model.getShapes(), "shapes.dat");
                    break;
                case 7:
                    List<Shape> loadedShapes = ShapeFileHandler.loadShapesFromFile("shapes.dat");
                    if (loadedShapes != null) {
                        model.getShapes().clear();
                        model.getShapes().addAll(loadedShapes);
                    }
                    break;
                case 8:
                    exitMenu = true;
                    break;
                default:
                    System.out.println("На жаль, такого варіанту немає. Спробуйте ще раз!");
            }
        }
    }
}

public class Main
{
    public static void main(String[] args)
    {
        Model model = new Model();
        View view = new View();
        Controller controller = new Controller(model, view);

        model.addShape(new Rectangle("Green", 8, 9));
        model.addShape(new Rectangle("Blue", 6, 2));
        model.addShape(new Circle("Blue", 7));
        model.addShape(new Circle("Red", 4));
        model.addShape(new Circle("Yellow", 5));
        model.addShape(new Triangle("Blue", 6, 8));
        model.addShape(new Triangle("Red", 9, 4));

        controller.controlMenu();
    }
}