class AddProjectToPackage < ActiveRecord::Migration[5.1]
  def change
    add_reference :packages, :project, foreign_key: true
  end
end
