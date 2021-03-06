package aformela.zad03.service;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import aformela.zad03.domain.Bird;

public class BirdService {
	private Connection connection;

	private List<Bird> birds = new ArrayList<Bird>();
    private String url = "jdbc:hsqldb:hsql://localhost/workdb";

    private String createTableBird = "CREATE TABLE Bird(id bigint GENERATED BY DEFAULT AS IDENTITY, name varchar(30) UNIQUE , date_of_birth date, is_female boolean, weight double, count_of_colors int)";

    private PreparedStatement addBirdSt;
    private PreparedStatement deleteBirdByIdSt;
    private PreparedStatement deleteAllBirdsSt;
    private PreparedStatement getAllBirdsSt;
    private PreparedStatement getBirdByNameSt;
    private Statement statement;

    public BirdService(){
        try{
           connection = DriverManager.getConnection(url);
           statement = connection.createStatement();

            ResultSet resultSet = connection.getMetaData().getTables(null,null,null,null);
            boolean tableExists = false;
            while (resultSet.next()){
                if("Bird".equalsIgnoreCase(resultSet.getString("table_name"))){
                    tableExists = true;
                    break;
                }
            }
            if(!tableExists){
                statement.executeUpdate(createTableBird);
            }
            addBirdSt = connection.prepareStatement("INSERT INTO Bird (name, date_of_birth, is_female, weight, count_of_colors) VALUES (?, ?, ?, ?, ?)");
            deleteBirdByIdSt = connection.prepareStatement("DELETE FROM Bird WHERE name=?");
            deleteAllBirdsSt = connection.prepareStatement("DELETE FROM Bird");
            getAllBirdsSt = connection.prepareStatement("SELECT id, name, date_of_birth, is_female, weight, count_of_colors FROM Bird");
            getBirdByNameSt = connection.prepareStatement("SELECT id, name, date_of_birth, is_female, weight, count_of_colors FROM Bird WHERE name=?");

        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    
    public Bird findByName(String name) throws SQLException {
    	Bird b = new Bird();
    	try {
    		//connection.setAutoCommit(false);
    		getBirdByNameSt.setString(1,name);
    		ResultSet resultSet = getBirdByNameSt.executeQuery();

    		while (resultSet.next()) {
                b.setId(resultSet.getInt("id"));
                b.setFemale(resultSet.getBoolean("is_female"));
                b.setName(resultSet.getString("name"));
                b.setDateOfBirth(resultSet.getString("date_of_birth"));
                b.setCountOfColors(resultSet.getInt("count_of_colors"));
                b.setWeight(resultSet.getDouble("weight"));
            }

    		
    		//connection.commit();
		} catch (SQLException e) {
		    e.printStackTrace();
		}
    	return b;
    }
    
    public boolean addAllBirds(List<Bird> birds) throws SQLException {
    	try {
    		connection.setAutoCommit(false);
    		for(Bird bird: birds) {
    			addBirdSt.setString(1, bird.getName());
            	addBirdSt.setString(2, bird.getDateOfBirth());
            	addBirdSt.setBoolean(3, bird.isFemale());
            	addBirdSt.setDouble(4, bird.getWeight());
            	addBirdSt.setInt(5, bird.getCountOfColors());

                addBirdSt.executeUpdate();
        	}
    		connection.commit();
    		return true;
		} catch (Exception e) {
			System.out.println("Wycofanie transakcji");
			try {
				connection.rollback();
			} catch (Exception e2) {
				System.out.println("Ratunku!");
			}
		}
    	
    	return false;
    }
    
    public int addBird(Bird bird) throws SQLException {
    	int count = 0;
        try {
        	addBirdSt.setString(1, bird.getName());
        	addBirdSt.setString(2, bird.getDateOfBirth());
        	addBirdSt.setBoolean(3, bird.isFemale());
        	addBirdSt.setDouble(4, bird.getWeight());
        	addBirdSt.setInt(5, bird.getCountOfColors());

            count = addBirdSt.executeUpdate();

        } catch (SQLException e) {
        	System.out.println("Wycofanie transakcji addBird");
			try {
				connection.rollback();
			} catch (Exception e2) {
				System.out.println("Ratunku!");
			}
            //e.printStackTrace();
        }
        return count;
    }
    
    public List<Bird> getAllBirds(){
    	List<Bird> birds = new ArrayList<>();
        try {
            ResultSet resultSet = getAllBirdsSt.executeQuery();

            while (resultSet.next()) {
                Bird b = new Bird();
                b.setId(resultSet.getInt("id"));
                b.setFemale(resultSet.getBoolean("is_female"));
                b.setName(resultSet.getString("name"));
                b.setDateOfBirth(resultSet.getString("date_of_birth"));
                b.setCountOfColors(resultSet.getInt("count_of_colors"));
                b.setWeight(resultSet.getDouble("weight"));
                birds.add(b);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    
    	return birds;
    }

    public int deleteAllBirds(){
        int count = 0;
        try {
            count = deleteAllBirdsSt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }
    
    public int deleteBird(Bird bird) throws SQLException {
    	int count = 0;
        try {
        	deleteBirdByIdSt.setString(1, bird.getName());

            count = deleteBirdByIdSt.executeUpdate();

        } catch (SQLException e) {
        	System.out.println("Wycofanie transakcji deleteBird");
			try {
				connection.rollback();
			} catch (Exception e2) {
				System.out.println("Ratunku!");
			}
            //e.printStackTrace();
        }
        return count;
    	
    }
}
