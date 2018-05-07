package com.ominiyi.filters;

import javax.servlet.annotation.WebFilter;
import com.googlecode.objectify.ObjectifyFilter;

/**
 * The ObjectifyWebFilter class is a filter used by the Objectify framework.
 *
 * @author  Adedayo Ominiyi
 */
@WebFilter(urlPatterns = {"/*"})
public class ObjectifyWebFilter extends ObjectifyFilter {}
