viadee<-function(graph)
{
  vc=t(c("#DFAD47","#7EBCA9"))
  vg <- rasterGrob(vc, width=unit(1,"npc"), height = unit(1,"npc"), 
                   interpolate = TRUE) # Colored Background
  sl <- grid.polygon(x=c(0.00, 0.90, 0.91, 0.94, 0.0), 
                     y=c(0.98, 0.98, 0.96, 1.00, 1.0), draw=FALSE,
                     gp=gpar(col="#D5E4EB", fill="#D5E4EB")) # Relative coordinates i.e. "percentages" where (0,0) is the bottom left
  
  return( graph 
          + theme_economist() 
          + annotation_custom(vg, xmin=-Inf, xmax=Inf, ymin=-Inf, ymax=Inf)
          + annotation_custom(sl, xmin=-Inf, xmax=Inf, ymin=-Inf, ymax=Inf)
          + theme(
            axis.text.x = element_text(size=7),
            axis.text.y = element_text(size=7),
            axis.title.x = element_text(size=10),
            axis.title.y = element_text(size=10),
            legend.text = element_text(size=5))
  )
}