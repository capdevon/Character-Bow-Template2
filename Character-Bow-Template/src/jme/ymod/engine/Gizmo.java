/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jme.ymod.engine;

import java.util.List;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Spline;
import com.jme3.math.Spline.SplineType;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Curve;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Line;
import com.jme3.scene.shape.Sphere;

/**
 *
 */
public class Gizmo {

    /**
     *
     * @param am
     * @param points
     * @param color
     * @return
     */
    public static Node drawPath(AssetManager am, List<Vector3f> points, ColorRGBA color) {

        final Material mat = new Material(am, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);

        Curve curve = new Curve(new Spline(SplineType.CatmullRom, points, 0.3f, false), 16);
        Geometry splineGeo = new Geometry("Spline", curve);
        splineGeo.setMaterial(mat);

        Node rootPath = new Node("PathLine");
        rootPath.attachChild(splineGeo);

        for (int i = 0; i < points.size(); i++) {
            Vector3f p1 = points.get(i);

            if (i < points.size() - 1) {
                Vector3f p2 = points.get(i + 1);
                Line line = new Line(p1, p2);
                Geometry lineGeo = new Geometry("Path.Line.GeoMesh." + i, line);
                lineGeo.setMaterial(mat);
                rootPath.attachChild(lineGeo);
            }

            Sphere sphere = new Sphere(6, 6, 0.2f);
            Geometry sphereGeo = new Geometry("Path.Sphere.GeoMesh", sphere);
            sphereGeo.setLocalTranslation(p1);
            sphereGeo.setMaterial(mat);
            rootPath.attachChild(sphereGeo);
        }

        return rootPath;
    }

    /**
     *
     * @param am
     * @param points
     * @param radius
     * @param color
     * @param alpha
     * @return
     */
    public static Node drawPathSegment(AssetManager am, Vector3f[] points, ColorRGBA color, float alpha, float radius) {

        Material mat = new Material(am, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", new ColorRGBA(color.r, color.g, color.b, 0.16f * alpha));
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);

        Node rootPath = new Node("PathSegments");

        for (int i = 0; i < points.length; i++) {
            Vector3f a = points[i];
            Vector3f b = points[(i + 1) % points.length];
            Vector3f direction = b.subtract(a);
            float height = direction.length();

            Node pathNode = new Node();
            pathNode.lookAt(direction, Vector3f.UNIT_Y);
            pathNode.setLocalTranslation(a.add(direction.divide(2)));

            Cylinder mesh = new Cylinder(4, 20, radius, height, true);
            Geometry geo = new Geometry("Path.GeoMesh." + i, mesh);
            geo.setQueueBucket(RenderQueue.Bucket.Translucent);
            geo.setMaterial(mat);
            pathNode.attachChild(geo);
            rootPath.attachChild(pathNode);
        }

        return rootPath;
    }

}
