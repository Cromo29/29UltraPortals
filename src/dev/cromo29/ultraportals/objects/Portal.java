package dev.cromo29.ultraportals.objects;

import dev.cromo29.durkcore.specificutils.VectorUtil;
import dev.cromo29.durkcore.util.MakeItem;
import dev.cromo29.durkcore.util.ParticleEffect;
import dev.cromo29.durkcore.util.ParticleMaker;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.*;

public class Portal {

    private final Location location, direction;
    private final List<Location> circlePoints, particlePoints;
    private final Map<ArmorStand, Integer> stands;
    private final Color color;

    private ArmorStand armorStand;

    public Portal(Location location, Location direction, Color color) {
        this.color = color;
        this.direction = direction;
        this.circlePoints = new ArrayList<>();
        this.particlePoints = new ArrayList<>();
        this.stands = new HashMap<>();

        double radius = 2.25;
        double particles = radius * 35;

        Location portalLocation = location.clone();

        Location center = location.clone().add(0, 1, 0).subtract(0, 1.5, 0);
        center.setPitch(0);

        Location clonedLocation = portalLocation.clone();

        portalLocation.setDirection(center.getDirection().multiply(-0.1).normalize());
        this.location = portalLocation;

        double tau = 6.283185307179586 / particles;

        for (int index = 0; index < particles; ++index) {
            double point = index * tau;

            Vector vector = location.getDirection().clone().multiply(Math.cos(point) * radius);
            vector.setY(Math.sin(point) * radius);

            VectorUtil.rotateAroundAxisY(vector, Math.toRadians(90));
            clonedLocation.add(vector);

            Location currentLocation = new Location(clonedLocation.getWorld(), clonedLocation.getX(), clonedLocation.getY(), clonedLocation.getZ());

            if (index % 13 == 0 && particlePoints.size() < 4) {
                this.particlePoints.add(currentLocation);

                ArmorStand armorStand = currentLocation.getWorld().spawn(currentLocation.clone().subtract(0, 0.5, 0), ArmorStand.class);

                armorStand.setMarker(true);
                armorStand.setSmall(true);
                armorStand.setArms(false);
                armorStand.setVisible(false);
                armorStand.setGravity(false);
                armorStand.setRightArmPose(new EulerAngle(0, 0, Math.toRadians(323)));
                armorStand.setItemInHand(new MakeItem(Material.WOOL)
                        .setData(Objects.equals(color, Color.fromRGB(27, 38, 49)) ? 15 : 10)
                        .build());
                armorStand.setCanPickupItems(false);

                stands.put(armorStand, index);
            }

            this.circlePoints.add(currentLocation);
            clonedLocation.subtract(vector);
        }
    }

    public Location getLocation() {
        return location;
    }

    public Location getDirection() {
        return direction;
    }

    public List<Location> getCircle() {
        return circlePoints;
    }

    public List<Location> getParticlePoints() {
        return particlePoints;
    }

    public void removeStands() {
        stands.forEach((armorStand, index) -> armorStand.remove());
        armorStand.remove();
    }

    public void update(boolean enabled) {
        circlePoints.forEach(loc -> ParticleMaker.sendParticle(ParticleEffect.REDSTONE, loc, enabled ? color : Color.fromRGB(255, 153, 0), 1, true));

        if (!enabled) return;

        for (ArmorStand stand : stands.keySet()) {
            int index = stands.get(stand);
            Location point = circlePoints.get(index);

            stand.teleport(point.clone().add(location.toVector().subtract(point.toVector())
                    .multiply(2.5)).subtract(0.0, 0.5, 0.0));

            if (index >= circlePoints.size() - 1) stands.put(stand, 0);
             else stands.put(stand, index + 1);
        }

    }

    public void createStand() {
        this.armorStand = location.getWorld().spawn(location.clone().add(0, 0.5, 0), ArmorStand.class);

        armorStand.setArms(false);
        armorStand.setVisible(false);
        armorStand.setGravity(false);
    }

    public ArmorStand getArmorStand() {
        return armorStand;
    }
}
