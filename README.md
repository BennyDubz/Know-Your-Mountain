# Know-Your-Mountain
### Author: Ben Williams

## Purpose

I developed this application for my dad, who is a member of the Jackson Hole Ski Patrol. He is a doctor by trade, and is only able to work on the mountain once a week. This program is designed to help him and his coworkers to become more familiar with the Ski Patrol's map of the mountain.  

The patrollers have nearly 150 unique points on the mountain, whose names are unrelated to the ski runs, and are spread out. If these points had the same names as the ski runs, it'd be easy to memorize them: just ski. However, since they're different, patrollers had only two real options:

1. Look at the poorly-formatted paper version of this map and try to memorize it by staring at it.
2. Work at the mountain as much as possible for 20 years until you finally know these points by heart.

These two options are obviously terrible for new employees and for those, like my dad, who cannot work at the mountain every day of the winter. They need to know these in order to be able to effectively communicate on the job, so not knowing them can cause potentially significant time loss or communication failure. This program allows current and new patrollers to learn these points in a digestible way that allows them to learn them over time and quiz themselves.

## Design

I divided the program into two halves: "Display Map", and "Learn". "Learn" is the bulk of the program.

### Display Map

This mode simply displays the map with all the points on it. It's features are: 
1. It allows you to hide the ski runs to make the points stand out
2. You can drag around and zoom in on the map
3. Hovering over a point will show its name. It also is displayed at the top of the screen in bigger font.
4. Clicking on a point will show its name and keep it there after you are no longer hovering over the point

I purposely kept this simple, so that someone could just click it and find whatever point they are looking for. 

### Learn

Learn divides the points into 10 different areas, each consisting of 15 points. Learn itself is divided into four modes, "Look", "Easy Multiple Choice", "Hard Multiple Choice", and "Cumulative". There is an intro-screen where the user can look at the purpose of each mode before diving in, and the map has most of the features of "display map".

#### Look

Look allows you to view one area at a time, and show every point's name at once (this is not available in the normal display map to avoid clutter). The purpose here is to be able to look at and try to familiarize onself with the points before starting the other modes. Think of it like looking at the definitions on a vocabulary list before studying it.

#### Easy Multiple Choice

For the current area it shows four points at a time, and prompts the user to click on a certain point, and there is a tally of how many they have correct. If they are click on the wrong point, they are shown the correct point, along with the names of all four points. This is meant to be the first, more forgiving step into learning the points in an area.

#### Hard Multiple Choice

This mode is still limited to the current area, but shows all 15 points at a time instead of just four. When the user is incorrect, it shows the name of the one they clicked on along with the location of the correct point- but not of the other 13. Like the Easy Multiple Choice, they have a tally of their score.

#### Cumulative

This final mode has the same functionality as Hard Multiple Choice, but with a catch: it includes the points of a current area along with all the previous areas. For instance, this mode on Area 4 is basically a Hard Multiple Choice on 60 points from Areas 1, 2, 3 and 4. 

The true final test is the cumulative mode on Area 10, where you would be tested on all 150 points.

## Personal Thoughts and Possible Future Additions

I began developing this straight out of my second CS class ever, so I was relatively inexperienced and tried to learn how to build Java GUIs as I went. If I were to build it from scratch, I would have a much easier time dividing the GUI into its respective panels, cards, layouts, etc... . I wanted to be able to have it running by the 2022/23 ski season though, so scrapping the whole project and rebuilding it more cleanly was not on my to-do list.

### Near term
1. Find a solution to the clutter so that all the names can be shown on the map at once. Perhaps by looking for and moving intersecting JLabels.
2. Make it more efficient. For istance, when you move your mouse it loops over every point, checking if the mouse's coordinates overlap with the point's. I could make this more efficient by dividing the points into quadrants behind the scenes, but there are currently no performance issues with the program that I have seen, so I am not sure if it is necessary.
3. Add one more mode to learn to be able to pick multiple specific areas to learn, but not necessarily in a row. i.e. Areas 2, 6, and 9. 

### Long term
1. The Patrollers have a similar problem with their end of day routes, where they zig-zag down the mountain on specific lines, meeting a specific number of people at each specific stopping points, all with different names and spread throughout the mountain. The points are also not necessarily the same as the one's used on this program, so it would be a major extension.
2. Have a way to save progress with specific users
3. Have an easier way to update the program other than having to re-download it.
4. Leaderboard???

