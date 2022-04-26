import core.Line;
import core.Station;
import junit.framework.TestCase;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class RouteCalculatorTest extends TestCase {

    List<Station> route;

    final static String DATA_FILE = "/Users/serjstepanian/Yandex.Disk.localized/Java Homework" +
            "/java_basics/ExceptionsDebuggingAndTesting/homework_2/SPBMetro/src/test/java/map.json";

    static StationIndex stationIndex;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        route = new ArrayList<>();

        Line line1 = new Line(1,"First");
        Line line2 = new Line(2,"Second");
        Line line3 = new Line(3,"Third");

        route.add(new Station("First",line1));
        route.add(new Station("Second",line1));
        route.add(new Station("Third",line2));
        route.add(new Station("Fourth",line2));
        route.add(new Station("Fifth",line3));
        route.add(new Station("Sixth",line3));
    }

    public void testGetShortestRouteNoConnections(){

        List<Station> shortestRouteStations = new ArrayList<>();
        Line line1 = new Line(3,"Невско-Василеостровская");
        shortestRouteStations.add(new Station("Беговая", line1));
        shortestRouteStations.add(new Station("Новокрестовская", line1));
        shortestRouteStations.add(new Station("Приморская", line1));
        shortestRouteStations.add(new Station("Василеостровская", line1));

        RouteCalculator calculator = getRouteCalculator();
        Station from = stationIndex.getStation("Беговая");
        Station to = stationIndex.getStation("Василеостровская");
        List<Station> actual = calculator.getShortestRoute(from,to);
        List<Station> expected = shortestRouteStations;

        assertEquals(actual,expected);
    }

    public void testGetShortestRouteOneConnection(){

        List<Station>  shortestRouteStations = new ArrayList<>();
        Line line1 = new Line(3,"Невско-Василеостровская");
        Line line2 = new Line(2,"Московско-Петроградская");
        shortestRouteStations.add(new Station("Василеостровская", line1));
        shortestRouteStations.add(new Station("Гостиный двор", line1));
        shortestRouteStations.add(new Station("Невский проспект", line2));
        shortestRouteStations.add(new Station("Горьковская", line2));

        RouteCalculator calculator = getRouteCalculator();
        Station from = stationIndex.getStation("Василеостровская");
        Station to = stationIndex.getStation("Горьковская");
        List<Station> actual = calculator.getShortestRoute(from,to);
        List<Station> expected = shortestRouteStations;

        assertEquals(actual,expected);
    }

    public void testGetShortestRouteTwoConnection(){

        List<Station>  shortestRouteStations = new ArrayList<>();
        Line line1 = new Line(3,"Невско-Василеостровская");
        Line line2 = new Line(2,"Московско-Петроградская");
        Line line3 = new Line(5,"Фрунзенско-Приморская");
        shortestRouteStations.add(new Station("Василеостровская", line1));
        shortestRouteStations.add(new Station("Гостиный двор", line1));
        shortestRouteStations.add(new Station("Невский проспект", line2));
        shortestRouteStations.add(new Station("Сенная площадь", line2));
        shortestRouteStations.add(new Station("Садовая", line3));
        shortestRouteStations.add(new Station("Адмиралтейская", line3));

        RouteCalculator calculator = getRouteCalculator();
        Station from = stationIndex.getStation("Василеостровская");
        Station to = stationIndex.getStation("Адмиралтейская");
        List<Station> actual = calculator.getShortestRoute(from,to);
        List<Station> expected = shortestRouteStations;

        assertEquals(actual,expected);
    }

    public void testCalculateDuration(){
        double actual = RouteCalculator.calculateDuration(route);
        double expected = 14.5;

        assertEquals(expected,actual);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    private static RouteCalculator getRouteCalculator() {
        createStationIndex();
        return new RouteCalculator(stationIndex);
    }

    private static void createStationIndex() {
        stationIndex = new StationIndex();
        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonData = (JSONObject) parser.parse(getJsonFile());

            JSONArray linesArray = (JSONArray) jsonData.get("lines");
            parseLines(linesArray);

            JSONObject stationsObject = (JSONObject) jsonData.get("stations");
            parseStations(stationsObject);

            JSONArray connectionsArray = (JSONArray) jsonData.get("connections");
            parseConnections(connectionsArray);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static String getJsonFile() {
        StringBuilder builder = new StringBuilder();
        try {
            List<String> lines = Files.readAllLines(Paths.get(DATA_FILE));
            lines.forEach(line -> builder.append(line));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return builder.toString();
    }

    private static void parseLines(JSONArray linesArray) {
        linesArray.forEach(lineObject -> {
            JSONObject lineJsonObject = (JSONObject) lineObject;
            Line line = new Line(
                    ((Long) lineJsonObject.get("number")).intValue(),
                    (String) lineJsonObject.get("name")
            );
            stationIndex.addLine(line);
        });
    }


    private static void parseStations(JSONObject stationsObject) {
        stationsObject.keySet().forEach(lineNumberObject ->
        {
            int lineNumber = Integer.parseInt((String) lineNumberObject);
            Line line = stationIndex.getLine(lineNumber);
            JSONArray stationsArray = (JSONArray) stationsObject.get(lineNumberObject);
            stationsArray.forEach(stationObject ->
            {
                Station station = new Station((String) stationObject, line);
                stationIndex.addStation(station);
                line.addStation(station);
            });
        });
    }


    private static void parseConnections(JSONArray connectionsArray) {
        connectionsArray.forEach(connectionObject ->
        {
            JSONArray connection = (JSONArray) connectionObject;
            List<Station> connectionStations = new ArrayList<>();
            connection.forEach(item ->
            {
                JSONObject itemObject = (JSONObject) item;
                int lineNumber = ((Long) itemObject.get("line")).intValue();
                String stationName = (String) itemObject.get("station");

                Station station = stationIndex.getStation(stationName, lineNumber);
                if (station == null) {
                    throw new IllegalArgumentException("core.Station " +
                            stationName + " on line " + lineNumber + " not found");
                }
                connectionStations.add(station);
            });
            stationIndex.addConnection(connectionStations);
        });
    }

}
