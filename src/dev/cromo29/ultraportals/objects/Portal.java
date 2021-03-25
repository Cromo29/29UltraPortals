package dev.cromo29.ultraportals.objects;

import dev.cromo29.durkcore.SpecificUtils.VectorUtil;
import dev.cromo29.durkcore.Util.ParticleEffect;
import dev.cromo29.durkcore.Util.ParticleMaker;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Portal {

    private Location location, direction;
    private List<Location> circlePoints, particlePoints;
    private Color color;

    private ArmorStand armorStand;

    public Portal(Location location, Location direction, Color color) {
        this.color = color;
        this.direction = direction;
        this.circlePoints = new ArrayList<>();
        this.particlePoints = new ArrayList<>();

        double radius = 2.25;
        double particles = radius * 35;

        Location portalLocation = location.clone();

        Location center = location.clone().add(0, 1, 0).subtract(0, 1.5, 0);
        center.setPitch(0);

        Location clonedLocation = portalLocation.clone();

        portalLocation.setDirection(center.getDirection().multiply(-0.1).normalize());
        this.location = portalLocation;

        double tau = 6.283185307179586D / particles;

        for (double index = 0; index < particles; ++index) {
            double point = index * tau;

            Vector vector = location.getDirection().clone().multiply(Math.cos(point) * radius);
            vector.setY(Math.sin(point) * radius);

            VectorUtil.rotateAroundAxisY(vector, Math.toRadians(90));
            clonedLocation.add(vector);

            if (index % 13 == 0 && particlePoints.size() < 4)
                this.particlePoints.add(new Location(clonedLocation.getWorld(), clonedLocation.getX(), clonedLocation.getY(), clonedLocation.getZ()));

            this.circlePoints.add(new Location(clonedLocation.getWorld(), clonedLocation.getX(), clonedLocation.getY(), clonedLocation.getZ()));
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

    public void update(boolean enabled) {
        AtomicBoolean hasPlayer = new AtomicBoolean(false);

        location.getWorld().getNearbyEntities(location, 30, 30, 30).forEach(entity -> {

            if (entity.getType() == EntityType.PLAYER) hasPlayer.set(true);
        });

        if (!hasPlayer.get()) return;

        circlePoints.forEach(loc -> ParticleMaker.sendParticle(ParticleEffect.REDSTONE, loc, enabled ? color : Color.fromRGB(255, 153, 0), 1, true));

        if (!enabled) return;

        particlePoints.forEach(loc -> {
            Vector vector = location.toVector().subtract(loc.toVector());

            ParticleMaker.sendParticle(ParticleEffect.FIREWORKS_SPARK, loc.clone().add(0, 0.1, 0), vector, 0.1F, 50);
        });

        ParticleMaker.sendParticle(ParticleEffect.FIREWORKS_SPARK, location, 0.1F, 1, 50);

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
