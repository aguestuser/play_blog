$ ->
  $.get "/posts/get/", (index, post) ->
    $('#posts')
      .append $('<div/>')
        .attr 'class', 'post-title'
        .text post.title
      .append $('<div/>')
        .attr 'class', 'post-body'
        .text post.body