package com.u2d.model;

import com.u2d.type.composite.GeoPoint;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Jan 25, 2008
 * Time: 9:04:37 PM
 */
public interface MappableEO extends EObject
{
   public GeoPoint geoPosition();
}
