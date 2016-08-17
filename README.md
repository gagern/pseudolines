# Pseudolines

Pseudolines is a Java application used to display pseudoline arrangements
(as they occur in the context of oriented matroids)
in a visually pleasing fashion and to allow manipulating them.

## Goals

The project started as an expeiment at [TU München][M10], and the first goal
was a comparison of two different layouting algorithms.
Both are based on a network of balanced forces between points,
essentially as though these points were connected by springs.
One approach was to take both points of intersection and Bézier control points
into consideration for this, while the other approach only positioned
the points of intersection and then computed a pline through these.
The latter was visually more pleasing, so continuing development
of the former is unlikely.

Another goal was smooth transitions between different configurations.
This has been implemented for triangle flips.
There are a large number of other animations which would make sense:

* Flip triangles if one of the edges lies on the boundary
* Make another line the boundary line
* Remove an edge and close the gaps
* Interactively add an edge, expanding the diagram in the process
* Toggle whether the boundary counts as a line or not

I (the original author) have a good idea on how I'd like to handle
the animations for these, so if you are interested in any of this,
please contact me so we can discuss details.

There are some other things which would be nice to have, too:

* Display pseudo-hyperplane arrangements using cuts along selected hyperplanes
* Export generated diagrams as SVG (and perhaps other vector formats as well)
* Loading of chirotopes from external files (and perhaps other sources as well)
* Support non-uniform arrangements (more than two pseudolines intersecting in a given point)

## Status

Since this application is not part of any paid project (and never was),
and since the user base is presumably very small, there has been no active
development in quite some time.
But if you are interested in this application, and want to make use of the
code one way or another, feel free to do so and please drop me a line so
that I know the user base has increased somewhat.
And if there is any feature you'd like to see added,
please post [a ticket on GitHub][issues] and I'll let you know whether
I think I'll have time to address this or not.

## Running

If you got the code, a simple `ant run` should fetch all required dependencies,
compile all code and execute the application.
If it does not, please [report][issues] any problems you encounter.

The pseudoline arrangement you see upon launching the application
is currently hard-coded in a file called [`Catalog.java`][Catalog.java],
where it is expressed as a chirotope using a bunch of signs.
The comments show the order of signs, and each sign encodes the
relative orientation of the three involved pseudolines.
Edit the file to change the initial arrangement, or write some additional code
in order to initialize the view with an arrangement from some other source.

## License

The code is licensed under [the GPL 3+ license](LICENSE.txt).
Please share your modifications with me so I can collect all improvements.

[M10]: https://www-m10.ma.tum.de/
[issues]: https://github.com/gagern/pseudolines/issues
[Catalog.java]: src/main/java/de/tum/ma/gagern/pseudolines/Catalog.java
