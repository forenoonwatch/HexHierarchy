package strat.debug;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import strat.game.Army;
import strat.game.Game;
import strat.game.Nation;
import strat.game.Region;
import strat.game.relationships.Alliance;
import strat.sql.SQLConnection;

public class ImportGameToSQL {
	public static final String DB = "jdbc:mysql://localhost:3306/hexhierarchy?useSSL=false";
	public static final String USER = "hhadmin";
    public static final String PASSWORD = "admin";
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
		SQLConnection con = new SQLConnection(DB, USER, PASSWORD);
		
		Game game = new Game();
		game.load("hre.game");
		
		con.executeUpdate("DELETE FROM alliance");
		con.executeUpdate("DELETE FROM tradeagreement");
		con.executeUpdate("DELETE FROM war");
		
		con.executeUpdate("DELETE FROM relationshipmember");
		con.executeUpdate("DELETE FROM relationship");
		
		con.executeUpdate("DELETE FROM armymove");
		con.executeUpdate("DELETE FROM army");
		
		con.executeUpdate("DELETE FROM region");
		con.executeUpdate("DELETE FROM nation");
		
		PreparedStatement ps = con.prepareStatement("INSERT INTO nation (NationID, OwnerID, Name, RGB, Money, SpawnedArmies) VALUES (?, ?, ?, ?, ?, ?)");
		
		for (Nation n : game.getNations().values()) {
			ps.setInt(1, n.getNationID());
			ps.setLong(2, n.getOwner());
			ps.setString(3, n.getName());
			ps.setInt(4, n.getRGB() & 0xFFFFFF);
			ps.setInt(5, n.getMoney());
			ps.setInt(6, n.getSpawnedArmies());
			
			ps.executeUpdate();
		}
		
		
		ps = con.prepareStatement("INSERT INTO region (RegionID, OwnerID) VALUES (?, ?)");
		
		for (Region r : game.getMap().getRegions().values()) {
			ps.setInt(1, r.getRegionID());
			ps.setInt(2, r.getOwnerID());
			ps.executeUpdate();
		}
		
		ps = con.prepareStatement("INSERT INTO army (ArmyNumber, OwnerID, Infantry, Cavalry, Artillery, RemainingMoves, IsFighting, Q, R)"
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
		
		for (Army a : game.getArmies()) {
			ps.setInt(1, a.getArmyNumber());
			ps.setInt(2, a.getOwnerID());
			ps.setInt(3, a.getUnits("infantry"));
			ps.setInt(4, a.getUnits("cavalry"));
			ps.setInt(5, a.getUnits("artillery"));
			ps.setInt(6, a.getRemainingMoves());
			ps.setBoolean(7, a.isFighting());
			ps.setInt(8, a.getQ());
			ps.setInt(9, a.getR());
			
			ps.executeUpdate();
		}
		
		ps = con.prepareStatement("INSERT INTO armymove (ArmyID, MoveNumber, Q, R) VALUES (?, ?, ?, ?)");
		PreparedStatement getID = con.prepareStatement("SELECT ArmyID FROM army WHERE OwnerID = ? AND ArmyNumber = ?");
		
		for (Army a : game.getArmies()) {
			if (a.getPendingMoves().size() > 0) {
				getID.setInt(1, a.getOwnerID());
				getID.setInt(2, a.getArmyNumber());
				ResultSet r = getID.executeQuery();
				
				int aID = -1;
				
				if (r.next()) {
					aID = r.getInt(1);
					System.out.println(aID);
				}
				else {
					System.out.printf("Failed to fetch army data for %s army %d%n", a.getOwner().getName(), a.getArmyNumber());
					continue;
				}
				
				for (int i = 0; i < a.getPendingMoves().size(); ++i) {
					ps.setInt(1, aID);
					ps.setInt(2, i);
					ps.setInt(3, a.getPendingMoves().get(i).getQ());
					ps.setInt(4, a.getPendingMoves().get(i).getR());
					
					ps.executeUpdate();
				}
			}
		}
		
		PreparedStatement rel = con.prepareStatement("INSERT INTO relationship (Type) VALUES (?)");
		getID = con.prepareStatement("SELECT RelationshipID FROM relationship ORDER BY RelationshipID DESC");
		ps = con.prepareStatement("INSERT INTO alliance (RelationshipID, Name, RGB) VALUES (?, ?, ?)");
		PreparedStatement addMember = con.prepareStatement("INSERT INTO relationshipmember (RelationshipID, NationID) VALUES (?, ?)");
		
		for (Alliance a : game.getAlliances()) {
			rel.setInt(1, 0);
			rel.executeUpdate();
			
			ResultSet r = getID.executeQuery();
			
			int id = -1;
			
			if (r.next()) {
				id = r.getInt(1);
			}
			else {
				System.out.println("Failed to get latest relID");
				continue;
			}
			
			ps.setInt(1, id);
			ps.setString(2, a.getName());
			ps.setInt(3, a.getRGB() & 0xFFFFFF);
			
			ps.executeUpdate();
			
			for (Nation n : a.getNations()) {
				addMember.setInt(1, id);
				addMember.setInt(2, n.getNationID());
				
				addMember.executeUpdate();
			}
		}
		
		System.out.println("Finished");
		
		/*ResultSet r = con.executeQuery("SELECT * FROM nation");
		
		while (r.next()) {
			System.out.println(r.getString(1));
		}*/
	}
}
