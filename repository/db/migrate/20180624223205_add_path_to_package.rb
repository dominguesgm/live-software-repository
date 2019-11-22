class AddPathToPackage < ActiveRecord::Migration[5.1]
  def change
    add_column :packages, :package_path, :string
  end
end
